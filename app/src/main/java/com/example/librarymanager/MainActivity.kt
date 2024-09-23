package com.example.librarymanager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.librarymanager.databinding.ActivityMainBinding
import com.example.librarymanager.databinding.DialogAddBookBinding
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoExt
import com.example.librarymanager.recyclerview.BookAdapter
import com.example.librarymanager.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private lateinit var mainBinding: ActivityMainBinding
    var adapter: BookAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.lifecycleOwner = this
        mainBinding.viewModel = viewModel
        adapter = BookAdapter(
            { showDeleteBookDialog(it, ::update) },
            { startEditPage(it) }
        )
        mainBinding.adapter = adapter
        mainBinding.fab.setOnClickListener {
            showAddBookDialog(::update)
        }
        mainBinding.btSearch.setOnClickListener {
            viewModel.searchViewContent.value?.run {
                BookRepository.getInstance().showBooks(this, ::update)
            }
        }
        mainBinding.fab.setOnLongClickListener {
            startActivity(Intent(this, TestActivity::class.java))
            true
        }
        viewModel.searchViewContent.observe(this) {
            BookRepository.getInstance().showBooks(it, ::update)
        }
    }

    override fun onStart() {
        super.onStart()
        mainBinding.viewModel?.searchViewContent?.value?.let {
            BookRepository.getInstance().showBooks(it, ::update)
        }
    }

    private fun update(booksInfo: List<BookInfo>) {
        adapter?.updateData(booksInfo)
    }

    private fun showAddBookDialog(onAdded: (List<BookInfo>) -> Unit) {
        val dialog = AlertDialog.Builder(this).create()
        val binding = DataBindingUtil.inflate<DialogAddBookBinding>(
            LayoutInflater.from(this), R.layout.dialog_add_book,
            null,
            false
        )
        dialog.setView(binding.root)

        dialog.setButton(
            DialogInterface.BUTTON_POSITIVE,
            this.getString(R.string.add_book_save)
        ) { _, _ ->
            if (binding.title.isNullOrEmpty()
                || binding.author.isNullOrEmpty()
                || binding.publishYear.isNullOrEmpty()
                || binding.isbn.isNullOrEmpty()
            ) {
                Toast.makeText(this, R.string.add_book_info_required, Toast.LENGTH_SHORT).show()
            } else {
                BookRepository.getInstance().addNewBook(
                    binding.title, binding.author,
                    binding.publishYear?.toInt() ?: 0,
                    binding.isbn, onAdded)
            }
        }

        dialog.show()
    }

    private fun showDeleteBookDialog(id: Int?, onDeleted: (List<BookInfo>) -> Unit) {
        AlertDialog.Builder(this)
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

    private fun startEditPage(bookInfo: BookInfo) {
        val intent = Intent(this, EditActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val bundle = Bundle()
        bookInfo.id?.run {
            bundle.putInt(BookInfoExt.COLUMN_ID, this)
        }
        bundle.putString(BookInfoExt.COLUMN_TITLE, bookInfo.title)
        bundle.putString(BookInfoExt.COLUMN_AUTHOR, bookInfo.author)
        bookInfo.publishYear?.run {
            bundle.putInt(BookInfoExt.COLUMN_PUBLISH_YEAR, this)
        }
        bundle.putString(BookInfoExt.COLUMN_ISBN, bookInfo.isbn)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}