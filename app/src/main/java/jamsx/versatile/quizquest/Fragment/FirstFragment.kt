package jamsx.versatile.quizquest.Fragment

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import jamsx.versatile.quizquest.Adapter.CategoryAdapter
import jamsx.versatile.quizquest.R
import jamsx.versatile.quizquest.databinding.FragmentFirstBinding

class FirstFragment : Fragment() , CategoryAdapter.TriviaItemClickInterface{

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private var arraylist: Array<String> = arrayOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        loadData()
        return binding.root
    }

    private fun loadData() {
        val res: Resources = resources
        arraylist = res.getStringArray(R.array.quiz_categories)

        for (value in arraylist) {
            categoryAdapter = CategoryAdapter(arraylist, this@FirstFragment, requireContext())
            binding.rview.adapter = categoryAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override suspend fun onItemClick(categoryItems: String) {
        val modifiedString = categoryItems.replace(" Trivia", "")

        val bundle = Bundle().apply {
            putString("Items", modifiedString)
        }

        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        Log.e("categoryItems1", modifiedString)
    }

}