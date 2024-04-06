package com.example.librarymanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.librarymanager.databinding.DialogAddBookBinding
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoDatabase
import com.example.librarymanager.db.BookInfoExt.COLUMN_AUTHOR
import com.example.librarymanager.db.BookInfoExt.COLUMN_ID
import com.example.librarymanager.db.BookInfoExt.COLUMN_ISBN
import com.example.librarymanager.db.BookInfoExt.COLUMN_PUBLISH_YEAR
import com.example.librarymanager.db.BookInfoExt.COLUMN_TITLE
import com.example.librarymanager.recyclerview.BookAdapter
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class BookCenter {

    private val context = BookApplication.getContext()
    private val dao = BookInfoDatabase.getInstance(context).bookInfoDao()
    // Use a single thread for the database operation.
    private val bookDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val bookScope = CoroutineScope(bookDispatcher)
    // For showing a dialog. When the activity is destroyed, clear it.
    var mainActivity: MainActivity? = null

    fun addNewBook() {
        mainActivity?.let { activity ->
            val dialog = AlertDialog.Builder(activity).create()
            val binding = DataBindingUtil.inflate<DialogAddBookBinding>(LayoutInflater.from(activity), R.layout.dialog_add_book, null, false)
            dialog.setView(binding.root)

            dialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                activity.getString(R.string.add_book_save)
            ) { _, _ ->
                if (binding.title.isNullOrEmpty()
                    || binding.author.isNullOrEmpty()
                    || binding.publishYear.isNullOrEmpty()
                    || binding.isbn.isNullOrEmpty()
                ) {
                    Toast.makeText(context, R.string.add_book_info_required, Toast.LENGTH_SHORT).show()
                } else {
                    bookScope.launch {
                        BookInfo().let {
                            it.title = binding.title
                            it.author = binding.author
                            it.publishYear = binding.publishYear?.toInt() ?: 0
                            it.isbn = binding.isbn
                            dao.insert(it)
                        }
                    }
                }
            }

            dialog.show()
        }
    }

    fun deleteBookInfo(id: Int?) {
        mainActivity?.let { activity ->
            AlertDialog.Builder(activity)
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    id?.let {
                        bookScope.launch {
                            dao.deleteBookById(it)
                            val booksInfo = dao.getBooks()
                            withContext(Dispatchers.Main) {
                                mainActivity?.adapter?.updateData(booksInfo)
                            }
                        }
                    }
                }
                .setNegativeButton(android.R.string.cancel) { obj, _ ->
                    obj.dismiss()
                }
                .show()
        }
    }

    fun showBooks(keyword: String) {
        bookScope.launch {
            val booksInfo = dao.getBooksByIsbn(keyword)
            withContext(Dispatchers.Main) {
                mainActivity?.adapter?.updateData(booksInfo)
            }
        }
    }

    fun startEditPage(bookInfo: BookInfo) {
        val intent = Intent(context, EditActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(COLUMN_ID, bookInfo.id)
        intent.putExtra(COLUMN_TITLE, bookInfo.title)
        intent.putExtra(COLUMN_AUTHOR, bookInfo.author)
        intent.putExtra(COLUMN_PUBLISH_YEAR, bookInfo.publishYear)
        intent.putExtra(COLUMN_ISBN, bookInfo.isbn)
        context.startActivity(intent)
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