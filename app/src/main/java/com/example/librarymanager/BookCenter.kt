package com.example.librarymanager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.widget.EditText
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoDatabase
import com.example.librarymanager.db.BookInfoExt.COLUMN_AUTHOR
import com.example.librarymanager.db.BookInfoExt.COLUMN_ISBN
import com.example.librarymanager.db.BookInfoExt.COLUMN_PUBLISH_YEAR
import com.example.librarymanager.db.BookInfoExt.COLUMN_TITLE
import com.example.librarymanager.recyclerview.BookAdapter
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class BookCenter {

    private val context = BookApplication.getContext()
    private val dao = BookInfoDatabase.getInstance(context).bookInfoDao()
    private val bookDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val bookScope = CoroutineScope(bookDispatcher)

    fun addNewBook(activity: Activity) {
        val dialog = AlertDialog.Builder(activity).create()
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_add_book, null)
        dialog.setView(view)

        val etTitle = view.findViewById<EditText>(R.id.et_dialog_title)
        val etAuthor = view.findViewById<EditText>(R.id.et_dialog_author)
        val etPublishYear = view.findViewById<EditText>(R.id.et_dialog_publish_year)
        val etIsbn = view.findViewById<EditText>(R.id.et_dialog_isbn)

        dialog.setButton(
            DialogInterface.BUTTON_POSITIVE,
            activity.getString(R.string.add_book_save)
        ) { _, _ ->
            val title = etTitle.text.toString()
            val author = etAuthor.text.toString()
            val publishYear = Integer.parseInt(etPublishYear.text.toString())
            val isbn = etIsbn.text.toString()
            bookScope.launch {
                BookInfo().let {
                    it.title = title
                    it.author = author
                    it.publishYear = publishYear
                    it.isbn = isbn
                    dao.insert(it)
                }
            }
        }

        dialog.show()
    }

    fun showBooks(adapter: BookAdapter) {
        bookScope.launch {
            val booksInfo = dao.getBooks()
            withContext(Dispatchers.Main) {
                adapter.updateData(booksInfo)
            }
        }
    }

    fun startEditPage(info: BookInfo) {
        val intent = Intent(context, EditActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(COLUMN_TITLE, info.title)
        intent.putExtra(COLUMN_AUTHOR, info.author)
        intent.putExtra(COLUMN_PUBLISH_YEAR, info.publishYear)
        intent.putExtra(COLUMN_ISBN, info.isbn)
        context.startActivity(intent)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var bookCenter: BookCenter? = null

        fun getInstance(): BookCenter {
            bookCenter?.run { return this }
            synchronized(this) {
                BookCenter().run {
                    bookCenter = this
                    return this
                }
            }
        }
    }
}