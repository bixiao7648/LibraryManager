package com.example.dbdemo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookInfo::class],
    version = 1,
    exportSchema = true
)
abstract class BookInfoDatabase : RoomDatabase() {
    abstract fun bookInfoDao(): BookInfoDao

    companion object {

        private const val DATABASE_FILE_NAME = "BOOK_INFO_DATABASE"

        @Volatile
        private var bookInfoDatabase: BookInfoDatabase? = null

        fun getInstance(context: Context) : BookInfoDatabase {
            bookInfoDatabase?.run {
                return this
            }

            synchronized(this) {
                Room.databaseBuilder(
                        context,
                        BookInfoDatabase::class.java,
                        DATABASE_FILE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build().also {
                        bookInfoDatabase = it
                        return it
                    }
            }
        }

    }
}