package com.example.librarymanager

import android.annotation.SuppressLint
import android.widget.Toast
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoDatabase
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class BookRepository {

    private val context = BookApplication.getContext()
    private val dao = BookInfoDatabase.getInstance(context).bookInfoDao()
    // Use a single thread for the database operation.
    private val bookDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val bookScope = CoroutineScope(bookDispatcher)

    fun addNewBook(
        title: String?,
        author: String?,
        publishYear: Int,
        isbn: String?,
        onAdded: (List<BookInfo>) -> Unit
    ) {
        bookScope.launch {
            BookInfo().let {
                it.title = title
                it.author = author
                it.publishYear = publishYear
                it.isbn = isbn
                dao.insert(it)
            }
            val booksInfo = dao.getBooks()
            withContext(Dispatchers.Main) {
                onAdded.invoke(booksInfo)
            }
        }
    }

    fun deleteBookInfo(id: Int, onDeleted: (List<BookInfo>) -> Unit) {
        bookScope.launch {
            dao.deleteBookById(id)
            val booksInfo = dao.getBooks()
            withContext(Dispatchers.Main) {
                onDeleted.invoke(booksInfo)
            }
        }
    }

    fun showBooks(keyword: String, onQueryCompleted: (List<BookInfo>) -> Unit) {
        bookScope.launch {
            val booksInfo = dao.getBooksByIsbn(keyword)
            withContext(Dispatchers.Main) {
                onQueryCompleted.invoke(booksInfo)
            }
        }
    }

    fun updateBookInfo(bookInfo: BookInfo) {
        bookScope.launch {
            dao.update(bookInfo)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.data_updated, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var bookRepository: BookRepository? = null

        fun getInstance(): BookRepository {
            bookRepository?.run { return this }
            synchronized(this) {
                BookRepository().run {
                    bookRepository = this
                    return this
                }
            }
        }
    }
}