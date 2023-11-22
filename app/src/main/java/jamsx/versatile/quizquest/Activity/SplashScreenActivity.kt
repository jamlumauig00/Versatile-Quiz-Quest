package jamsx.versatile.quizquest.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import jamsx.versatile.quizquest.R
import jamsx.versatile.quizquest.Model.TriviaQuestionModel


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var questionList: List<TriviaQuestionModel>
    private val delayMillis = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        /*databaseReference = FirebaseDatabase.getInstance().getReference("data")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        // Access the data with ID "0"
                        val dataModel: TriviaQuestionModel? =
                            snapshot.getValue(TriviaQuestionModel::class.java)
                        if (dataModel != null) {
                            if (dataModel.id == 0) {
                                if (dataModel.question == "true") {
                                    Log.e("dataModel", "$dataModel")
                                    nextActivity("Game", dataModel.category)
                                } else {
                                    nextActivity("Main", dataModel.category)
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })*/


        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java).apply {
                // If you need to pass data to the next activity, you can do it here
            })
            finish()
        }, delayMillis)
    }

    fun nextActivity(nameActivity: String, category: String) {

        val intentClass = when (nameActivity) {
            "Game" -> QuizWebActivity::class.java
            else -> MainActivity::class.java
        }

        Handler().postDelayed({
            startActivity(Intent(this, intentClass).apply {
                // If you need to pass data to the next activity, you can do it here
                putExtra("category", category)
            })
            finish()
        }, delayMillis)
    }
}