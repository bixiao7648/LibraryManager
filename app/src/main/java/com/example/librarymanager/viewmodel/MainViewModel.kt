package com.example.librarymanager.viewmodel

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.librarymanager.BookRepository
import com.example.librarymanager.EditActivity
import com.example.librarymanager.R
import com.example.librarymanager.databinding.DialogAddBookBinding
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoExt

class MainViewModel : ViewModel() {
    var searchViewContent: MutableLiveData<String> = MutableLiveData()

    init {
        searchViewContent.value = ""
    }

    fun showAddBookDialog(activity: Activity, onAdded: (List<BookInfo>) -> Unit) {
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
                Toast.makeText(activity, R.string.add_book_info_required, Toast.LENGTH_SHORT).show()
            } else {
                BookRepository.getInstance().addNewBook(
                    binding.title, binding.author,
                    binding.publishYear?.toInt() ?: 0,
                    binding.isbn, onAdded)
            }
        }

        dialog.show()
    }

    fun showDeleteBookDialog(activity: Activity, id: Int?, onDeleted: (List<BookInfo>) -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                id?.let {
                    BookRepository.getInstance().deleteBookInfo(it, onDeleted)
                }
            }
            .setNegativeButton(android.R.string.cancel) { obj, _ ->
                obj.dismiss()
            }
            .show()
    }

    fun startEditPage(context: Context, bookInfo: BookInfo) {
        val intent = Intent(context, EditActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(BookInfoExt.COLUMN_ID, bookInfo.id)
        intent.putExtra(BookInfoExt.COLUMN_TITLE, bookInfo.title)
        intent.putExtra(BookInfoExt.COLUMN_AUTHOR, bookInfo.author)
        intent.putExtra(BookInfoExt.COLUMN_PUBLISH_YEAR, bookInfo.publishYear)
        intent.putExtra(BookInfoExt.COLUMN_ISBN, bookInfo.isbn)
        context.startActivity(intent)
    }
}