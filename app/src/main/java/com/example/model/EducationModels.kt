package com.example.model

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    SHORT_ANSWER,
    APPLIED
}

data class QuizQuestion(
    val id: String,
    val text: String,
    val type: QuestionType,
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val isTrue: Boolean = true,
    val modelAnswer: String = "",
    val explanation: String = "",
    val topicTag: String = ""
)

data class PracticeExercise(
    val id: String,
    val title: String,
    val question: String,
    val hint: String,
    val solution: String,
    val exampleQuranVerse: String = ""
)

data class TermDefinition(
    val term: String,
    val definition: String,
    val sourceOrContext: String = ""
)

data class AppliedExample(
    val title: String,
    val quranVerse: String,
    val surahAndAyah: String,
    val hamzahRule: String,
    val khalafRule: String,
    val khalladRule: String,
    val poeticEvidence: String = ""
)

data class LessonContent(
    val objectives: List<String>,
    val coreConcepts: List<String>,
    val terms: List<TermDefinition>,
    val academicExplanation: String,
    val tayyibahVerses: List<String>,
    val simplifiedExplanation: String,
    val appliedExamples: List<AppliedExample>,
    val practiceExercises: List<PracticeExercise>,
    val multipleChoiceQuestions: List<QuizQuestion>,
    val trueFalseQuestions: List<QuizQuestion>,
    val shortAnswerQuestions: List<QuizQuestion>,
    val appliedQuestions: List<QuizQuestion>
)

data class Lesson(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val chapterId: String,
    val unitId: String,
    val content: LessonContent
)

data class Chapter(
    val id: String,
    val number: Int,
    val title: String,
    val description: String,
    val unitId: String,
    val lessons: List<Lesson>
)

data class Unit(
    val id: String,
    val number: Int,
    val title: String,
    val description: String,
    val chapters: List<Chapter>
)

data class Exam(
    val id: String,
    val title: String,
    val type: ExamType, // LESSON, UNIT, FINAL
    val targetId: String, // lessonId or unitId or "final"
    val questions: List<QuizQuestion>,
    val timeMinutes: Int = 15
)

enum class ExamType {
    LESSON_QUIZ,
    UNIT_EXAM,
    FINAL_EXAM
}

data class ExamResult(
    val examId: String,
    val examTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val missedTopicTags: List<String> = emptyList()
)

data class StudentProfile(
    val id: String = "std_1",
    val name: String = "محمد عبد الرحمن",
    val gradeLevel: String = "قسم القراءات - المرحلة التخصصية",
    val completedLessonIds: Set<String> = emptySet(),
    val examResults: List<ExamResult> = emptyList(),
    val lastStudiedLessonId: String? = null
)

data class Textbook(
    val id: String,
    val title: String,
    val author: String,
    val gradeLevel: String,
    val description: String,
    val units: List<Unit>,
    val coverDrawableRes: Int
)

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val isExternalKnowledge: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    STUDENT,
    AI_TUTOR
}
