package futur.apps.composeproject1.quiz.data.demo

import futur.apps.composeproject1.utils.Question


/**
 * ============================================================
 * Data.kt
 *
 * Demo questions used in the Quiz App.
 *
 * 🔹 Buyers Notes:
 * - This is sample data for testing and preview purposes.
 * - You can replace, add, or remove questions easily.
 * - Each Question contains:
 *     • text: The question string
 *     • options: List of multiple choice answers
 *     • correctAnswer: The correct answer string
 * ============================================================
 */
object Data {

    /** List of dummy questions for testing or demo */
    val dummyQuestions = listOf(
        Question(
            "What is the capital of France?",
            listOf("Paris", "London", "Berlin", "Madrid"),
            "Paris"
        ),
        Question(
            "Which planet is known as the Red Planet?",
            listOf("Mars", "Venus", "Jupiter", "Saturn"),
            "Mars"
        ),
        Question(
            "What is the largest mammal in the world?",
            listOf("Elephant", "Blue Whale", "Giraffe", "Hippopotamus"),
            "Blue Whale"
        ),
        Question(
            "Who painted the Mona Lisa?",
            listOf("Leonardo da Vinci", "Pablo Picasso", "Vincent van Gogh", "Michelangelo"),
            "Leonardo da Vinci"
        ),
        Question(
            "What is the largest ocean on Earth?",
            listOf("Atlantic", "Indian", "Pacific", "Arctic"),
            "Pacific"
        ),
         Question(
             "Which country is known as the Land of the Rising Sun?",
             listOf("China", "Japan", "South Korea", "Thailand"),
             "Japan"
         ),
         Question(
             "What is the smallest country by land area?",
             listOf("Vatican City", "Monaco", "San Marino", "Liechtenstein"),
             "Vatican City"
         ),
         Question(
             "Which gas do plants use for photosynthesis?",
             listOf("Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen"),
             "Carbon Dioxide"
         ),
         Question(
             "What is the largest organ in the human body?",
             listOf("Heart", "Liver", "Skin", "Brain"),
             "Skin"
         ),
         Question(
             "Which river is the longest in the world?",
             listOf("Nile", "Amazon", "Yangtze", "Mississippi"),
             "Nile"
         )
    )
}