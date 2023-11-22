package jamsx.versatile.quizquest.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jamsx.versatile.quizquest.databinding.QuizListItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryAdapter(
    private var list: Array<String>,
    val ItemClickInterface: TriviaItemClickInterface,
    val context: Context
) : RecyclerView.Adapter<CategoryAdapter.TriviaViewHolder>() {

    inner class TriviaViewHolder(private val binding: QuizListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("DiscouragedApi")
        fun bind(item: String ) {
            binding.Category.text = item

            val firstWord = item.split(" ").firstOrNull()?.lowercase() ?: ""

            val drawableResourceId = context.resources.getIdentifier(
                firstWord,
                "drawable",
                context.packageName
            )

            Log.e("drawableResourceId",  firstWord)

// Set the drawable to the ImageView
            if (drawableResourceId != 0) {
                binding.icon.setImageResource(drawableResourceId)
            } else {
                // Handle case where drawable is not found
                // You can set a default drawable or show an error message
            }

            binding.linearlayout.setOnClickListenerWithCoroutine {
                ItemClickInterface.onItemClick(item)
            }

        }
    }

    fun View.setOnClickListenerWithCoroutine(action: suspend () -> Unit) {
        setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                action()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TriviaViewHolder {
        val binding = QuizListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TriviaViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TriviaViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    interface TriviaItemClickInterface {
        suspend fun onItemClick(categoryItems: String)

    }


}

