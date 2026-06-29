package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getCount(): Int
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE subjectName = :subject")
    fun getLessonsBySubject(subject: String): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE subjectName = :subject AND unitName = :unit")
    fun getLessonsByUnit(subject: String, unit: String): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons")
    fun getAllLessons(): Flow<List<Lesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%'")
    fun searchLessons(query: String): Flow<List<Lesson>>
}

@Dao
interface MinisterialQuestionDao {
    @Query("SELECT * FROM ministerial_questions WHERE subject = :subject")
    fun getQuestionsBySubject(subject: String): Flow<List<MinisterialQuestion>>

    @Query("SELECT * FROM ministerial_questions WHERE subject = :subject AND unitName = :unit")
    fun getQuestionsByUnit(subject: String, unit: String): Flow<List<MinisterialQuestion>>

    @Query("SELECT * FROM ministerial_questions")
    fun getAllQuestions(): Flow<List<MinisterialQuestion>>

    @Query("SELECT DISTINCT unitName FROM ministerial_questions WHERE subject = :subject")
    suspend fun getUnitsBySubject(subject: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<MinisterialQuestion>)
}

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE code = :code")
    suspend fun getCardByCode(code: String): Card?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<Card>)

    @Update
    suspend fun updateCard(card: Card)
}

@Dao
interface StudyLogDao {
    @Query("SELECT * FROM study_logs ORDER BY timestamp DESC")
    fun getAllStudyLogs(): Flow<List<StudyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyLog(log: StudyLog)

    @Query("DELETE FROM study_logs")
    suspend fun clearLogs()
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Update
    suspend fun updateUserStats(stats: UserStats)
}
