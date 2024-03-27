package com.example.librarymanager.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(info: BookInfo)

    @Delete
    fun delete(info: BookInfo)

    @Update
    fun update(info: BookInfo)

    @Query("SELECT * from book")
    fun getBooks(): List<BookInfo>

    @Query("SELECT * from book WHERE id = :id")
    fun getBookById(id: Int): BookInfo?

    @Query("SELECT * from book WHERE title = :title")
    fun getBookByTitle(title: String): BookInfo?

    @Query("SELECT * from book WHERE isbn LIKE '%' || :keyword || '%' ")
    fun getBooksByIsbn(keyword: String): List<BookInfo>

    @Query("DELETE from book WHERE id = :id")
    fun deleteBookById(id: Int)
}