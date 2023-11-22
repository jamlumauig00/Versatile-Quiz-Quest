package jamsx.versatile.quizquest.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import jamsx.versatile.quizquest.R
import jamsx.versatile.quizquest.Model.TriviaQuestionModel
import jamsx.versatile.quizquest.databinding.FragmentSecondBinding
import java.util.concurrent.TimeUnit

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseReference: DatabaseReference
    private lateinit var questionList: List<TriviaQuestionModel>

    private var currentQuestionIndex = 0
    private var score = 0

    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 0
    private val countdownInterval: Long = 1000 // 1 second interval

    private var context: Context? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        context = requireContext()

        databaseReference = FirebaseDatabase.getInstance().getReference("data")
        val items = requireArguments().getString("Items")

        if (items != null) {
            timeLeftInMillis = TimeUnit.MINUTES.toMillis(3) // 5 minutes
            loadQuestionFromFirebase(items)
            startTimer()

        }
        binding.detailToolbar.setOnClickListener {
            currentQuestionIndex = 0
            score = 0
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        return binding.root
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, countdownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                // Timer finished, handle accordingly (e.g., quiz completion)
                showScore()
            }
        }.start()
    }


    private fun updateTimerText() {
        // Update your timer TextView with the formatted time remaining
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)

        // Assuming you have a TextView in your layout with id 'timerTextView'
        binding.timerTextView.text = formattedTime
    }


    private fun loadQuestionFromFirebase(item: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("DiscouragedApi")
            override fun onDataChange(snapshot: DataSnapshot) {
                val quizList = mutableListOf<TriviaQuestionModel>()
                val allQuestions =
                    snapshot.children.mapNotNull { it.getValue(TriviaQuestionModel::class.java) }
                val shuffledQuestions = allQuestions.shuffled()
                val selectedQuizQuestions = shuffledQuestions.take(20).shuffled()

                Log.e("item", item)
                // Shuffle the list of all questions

                for (quizQuestion in shuffledQuestions) {
                    if (quizQuestion.id != 0) {
                        if (item == quizQuestion.category && item != "General" && quizList.size < 20) {
                            Log.e("quizQuestion.category1", quizQuestion.category)
                            quizList.add(quizQuestion)
                        } else if (item == "General" && quizList.size < 20) {
                            Log.e("quizQuestion.category2", quizQuestion.category)
                            for (quizQuestions in selectedQuizQuestions) {
                                quizList.add(quizQuestions)
                            }
                        }
                    }
                }

                questionList = quizList
                loadQuestion()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("QuizActivity", "Error loading quiz questions", error.toException())
            }
        })
    }

    @SuppressLint("DiscouragedApi")
    private fun loadQuestion() {
        val currentQuestion = questionList[currentQuestionIndex]
        binding.idQuestion.text = currentQuestion.question
        binding.optionsLinearLayout.removeAllViews()

        val firstWord = currentQuestion.category.split(" ").firstOrNull()?.lowercase() ?: ""
        val drawableResourceId = context?.resources?.getIdentifier(
            firstWord,
            "drawable",
            context?.packageName
        )

        if (drawableResourceId != null && context != null) {
            binding.idCategoryImage.setImageResource(drawableResourceId)
        }
        binding.idCategoryName.text = currentQuestion.category

        val allDescriptions = listOf(
            currentQuestion.choiceA,
            currentQuestion.choiceB,
            currentQuestion.choiceC,
            currentQuestion.choiceD
        )

        val correctAnswer = currentQuestion.answer
        val shuffledOptions = (allDescriptions - correctAnswer).shuffled().take(3) + correctAnswer

        for (option in shuffledOptions) {
            val button = Button(context)
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            //layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.radio_button_margin_bottom)

            button.layoutParams = layoutParams
            button.text = option
            layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.your_button_padding)
            val defaultTextSize = resources.configuration.fontScale

            val scaleFactor = 20f
            button.textSize = defaultTextSize * scaleFactor
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            button.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_bg)
            button.setPadding(
                resources.getDimensionPixelSize(R.dimen.your_button_padding),
                resources.getDimensionPixelSize(R.dimen.your_button_padding),
                resources.getDimensionPixelSize(R.dimen.your_button_padding),
                resources.getDimensionPixelSize(R.dimen.your_button_padding)
            )
            button.setOnClickListener {
                onButtonClicked(button)
            }

            binding.optionsLinearLayout.addView(button)
        }
    }

    private fun onButtonClicked(button: Button) {
        val selectedAnswer = button.text.toString()
        val correctAnswer = questionList[currentQuestionIndex].answer
        if (selectedAnswer == correctAnswer) {
            score++
        }

        if (currentQuestionIndex < questionList.size - 1) {
            currentQuestionIndex++
            loadQuestion()
        } else {
            showScore()
        }
    }

    private fun showScore() {
        binding.questions.visibility = View.GONE
        binding.score.visibility = View.VISIBLE
        binding.timerTextView.visibility = View.GONE
        binding.scoretv.text = getString(R.string.quiz_completed, score, questionList.size)
        binding.optionsLinearLayout.removeAllViews()
        Log.e("score", score.toString())

        binding.submitButton.text = getString(R.string.play_again)
        binding.submitButton.setOnClickListener {
            resetQuiz()
        }
    }

    private fun resetQuiz() {
        currentQuestionIndex = 0
        score = 0
        context?.let {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
    }
}
