package com.example.librarymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.librarymanager.databinding.ActivityEditBinding
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoExt.COLUMN_AUTHOR
import com.example.librarymanager.db.BookInfoExt.COLUMN_ID
import com.example.librarymanager.db.BookInfoExt.COLUMN_ISBN
import com.example.librarymanager.db.BookInfoExt.COLUMN_PUBLISH_YEAR
import com.example.librarymanager.db.BookInfoExt.COLUMN_TITLE

class EditActivity : AppCompatActivity() {

    private lateinit var editBinding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit)
        setTitle(R.string.edit_page_title)
        initData()
    }

    private fun initData() {
        editBinding.activity = this
        editBinding.title = intent.getStringExtra(COLUMN_TITLE)
        editBinding.author = intent.getStringExtra(COLUMN_AUTHOR)
        editBinding.publishYear = intent.getIntExtra(COLUMN_PUBLISH_YEAR, 0).toString()
        editBinding.isbn = intent.getStringExtra(COLUMN_ISBN)
    }

    fun updateInfo() {
        val id = intent.getIntExtra(COLUMN_ID, -1)
        if (id < 0) {
            return
        }
        val title = editBinding.title
        val author = editBinding.author
        val publishYear = editBinding.publishYear?.toInt() ?: 0
        val isbn = editBinding.isbn
        BookInfo().let {
            it.id = id
            it.title = title
            it.author = author
            it.publishYear = publishYear
            it.isbn = isbn
            BookCenter.getInstance().updateBookInfo(it)
        }
    }
}