package com.example.icsproject2easenetics.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.User
import com.example.icsproject2easenetics.data.models.UserProgress

@Database(
    entities = [User::class, Lesson::class, UserProgress::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "easenetics_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}