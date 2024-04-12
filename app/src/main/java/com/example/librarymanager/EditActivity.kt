package com.example.librarymanager

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.librarymanager.databinding.ActivityEditBinding
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoExt.COLUMN_AUTHOR
import com.example.librarymanager.db.BookInfoExt.COLUMN_ID
import com.example.librarymanager.db.BookInfoExt.COLUMN_ISBN
import com.example.librarymanager.db.BookInfoExt.COLUMN_PUBLISH_YEAR
import com.example.librarymanager.db.BookInfoExt.COLUMN_TITLE
import com.example.librarymanager.viewmodel.EditViewModel

class EditActivity : AppCompatActivity() {

    private val viewModel: EditViewModel by lazy {
        ViewModelProvider(this)[EditViewModel::class.java]
    }
    private lateinit var editBinding: ActivityEditBinding
    private var currentItemId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit)
        editBinding.lifecycleOwner = this
        setTitle(R.string.edit_page_title)
        editBinding.btEditSave.setOnClickListener { updateInfo(currentItemId) }
        initData()
    }

    private fun initData() {
        editBinding.activity = this
        editBinding.viewModel = viewModel
        intent.extras?.run {
            currentItemId = getInt(COLUMN_ID, -1)
            viewModel.title = getString(COLUMN_TITLE, "")
            viewModel.author = getString(COLUMN_AUTHOR, "")
            viewModel.publishYear = getInt(COLUMN_PUBLISH_YEAR, 0).toString()
            viewModel.isbn = getString(COLUMN_ISBN, "")
        }
    }

    private fun updateInfo(id: Int) {
        if (id < 0) {
            return
        }
        BookInfo().let {
            it.id = id
            it.title = viewModel.title
            it.author = viewModel.author
            it.publishYear = viewModel.publishYear.toInt()
            it.isbn = viewModel.isbn
            BookRepository.getInstance().updateBookInfo(it) {
                editBinding.btEditSave.visibility = View.INVISIBLE
                finish()
            }
        }
    }
}