package jamsx.versatile.quizquest.Model

data class TriviaQuestionModel(
    val id: Int = 0,
    val category: String = "",
    val question: String = "",
    val choiceA: String = "",
    val choiceB: String = "",
    val choiceC: String = "",
    val choiceD: String = "",
    val answer: String = ""
)
