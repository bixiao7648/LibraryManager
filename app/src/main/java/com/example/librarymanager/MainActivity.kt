package com.example.librarymanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.librarymanager.databinding.ActivityMainBinding
import com.example.librarymanager.db.BookInfo
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
            { viewModel.showDeleteBookDialog(this, it, ::update) },
            { viewModel.startEditPage(this, it) }
        )
        mainBinding.adapter = adapter
        mainBinding.fab.setOnClickListener {
            viewModel.showAddBookDialog(this, ::update)
        }
        mainBinding.btSearch.setOnClickListener {
            viewModel.searchViewContent.value?.run {
                BookRepository.getInstance().showBooks(this, ::update)
            }
        }
        viewModel.searchViewContent.observe(this) {
            BookRepository.getInstance().showBooks(it, ::update)
        }
    }

    private fun update(booksInfo: List<BookInfo>) {
        adapter?.updateData(booksInfo)
    }
}