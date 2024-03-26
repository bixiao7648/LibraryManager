package com.example.librarymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.librarymanager.db.BookInfo
import com.example.librarymanager.db.BookInfoExt.COLUMN_AUTHOR
import com.example.librarymanager.db.BookInfoExt.COLUMN_ID
import com.example.librarymanager.db.BookInfoExt.COLUMN_ISBN
import com.example.librarymanager.db.BookInfoExt.COLUMN_PUBLISH_YEAR
import com.example.librarymanager.db.BookInfoExt.COLUMN_TITLE

class EditActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etPublishYear: EditText
    private lateinit var etIsbn: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        etTitle = findViewById(R.id.et_dialog_title)
        etAuthor = findViewById(R.id.et_dialog_author)
        etPublishYear = findViewById(R.id.et_dialog_publish_year)
        etIsbn = findViewById(R.id.et_dialog_isbn)
        findViewById<Button>(R.id.bt_edit_save).setOnClickListener {
            updateInfo()
        }
        initData()
    }

    private fun initData() {
        val title = intent.getStringExtra(COLUMN_TITLE)
        val author = intent.getStringExtra(COLUMN_AUTHOR)
        val publishYear = intent.getIntExtra(COLUMN_PUBLISH_YEAR, 0)
        val isbn = intent.getStringExtra(COLUMN_ISBN)
        etTitle.setText(title)
        etAuthor.setText(author)
        etPublishYear.setText(publishYear.toString())
        etIsbn.setText(isbn)
    }

    private fun updateInfo() {
        val id = intent.getIntExtra(COLUMN_ID, -1)
        if (id < 0) {
            return
        }
        val title = etTitle.text.toString()
        val author = etAuthor.text.toString()
        val publishYear = etPublishYear.text.toString().toInt()
        val isbn = etIsbn.text.toString()
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