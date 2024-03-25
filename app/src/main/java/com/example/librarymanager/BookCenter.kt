package com.example.librarymanager

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.EditText

class BookCenter {

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
            val publishYear = etPublishYear.text.toString()
            val isbn = etIsbn.text.toString()

        }

        dialog.show()
    }

    companion object {
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