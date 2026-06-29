package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectName: String, // e.g., "الفيزياء", "الكيمياء", "القرآن الكريم"
    val title: String,
    val description: String,
    val imageUrl: String
)

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val subjectName: String,
    val unitName: String,
    val title: String,
    val pageNumber: Int,
    val content: String, // Official book text used for grounding
    val summary: String  // Condensed study guide
)

@Entity(tableName = "ministerial_questions")
data class MinisterialQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val unitName: String,
    val lessonName: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // e.g., "A", "B", "C", "D"
    val explanation: String, // Step by step solution steps
    val year: Int, // Year of exam, e.g., 2023, 2024
    val difficulty: String, // "سهل", "متوسط", "صعب"
    val frequency: Int = 1 // How many times it appeared in past exams
)

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val code: String, // YMN-XXXX-XXXX
    val tier: String, // "FREE", "STUDENT", "PLUS", "VIP"
    val durationDays: Int,
    val deviceCount: Int,
    val isRedeemed: Boolean = false,
    val redeemDate: Long = 0L
)

@Entity(tableName = "study_logs")
data class StudyLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val queryText: String,
    val subject: String,
    val unitName: String,
    val lessonName: String,
    val confidence: Float,
    val matchedText: String,
    val responseText: String
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val solvedQuestionsCount: Int = 0,
    val correctAnswersCount: Int = 0,
    val subscriptionTier: String = "FREE",
    val subscriptionExpiry: Long = 0L,
    val deviceId: String = "YEM-DEVICE-X92F"
)
