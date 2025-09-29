package com.example.icsproject2easenetics.data.database

import androidx.room.*
import com.example.icsproject2easenetics.data.models.Lesson
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY `order` ASC")
    fun getAllLessons(): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE lessonId = :lessonId")
    fun getLesson(lessonId: String): Flow<Lesson?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLessons(lessons: List<Lesson>)
}