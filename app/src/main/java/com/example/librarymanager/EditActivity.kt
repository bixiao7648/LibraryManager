package com.example.librarymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit)
        setTitle(R.string.edit_page_title)
        editBinding.btEditSave.setOnClickListener { updateInfo() }
        initData()
    }

    private fun initData() {
        editBinding.activity = this
        editBinding.viewModel = viewModel
        viewModel.title = intent.getStringExtra(COLUMN_TITLE) ?: ""
        viewModel.author = intent.getStringExtra(COLUMN_AUTHOR) ?: ""
        viewModel.publishYear = intent.getIntExtra(COLUMN_PUBLISH_YEAR, 0).toString()
        viewModel.isbn = intent.getStringExtra(COLUMN_ISBN) ?: ""
    }

    fun updateInfo() {
        val id = intent.getIntExtra(COLUMN_ID, -1)
        if (id < 0) {
            return
        }
        BookInfo().let {
            it.id = id
            it.title = viewModel.title
            it.author = viewModel.author
            it.publishYear = viewModel.publishYear.toInt()
            it.isbn = viewModel.isbn
            BookCenter.getInstance().updateBookInfo(it)
        }
    }
}