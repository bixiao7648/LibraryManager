package com.example.librarymanager.viewmodel

import androidx.lifecycle.ViewModel
import com.example.librarymanager.BookRepository
import com.example.librarymanager.db.BookInfo

class EditViewModel : ViewModel() {
    var title = ""
    var author = ""
    var publishYear = ""
    var isbn = ""

    fun updateInfo(id: Int) {
        if (id < 0) {
            return
        }
        BookInfo().let {
            it.id = id
            it.title = title
            it.author = author
            it.publishYear = publishYear.toInt()
            it.isbn = isbn
            BookRepository.getInstance().updateBookInfo(it)
        }
    }
}