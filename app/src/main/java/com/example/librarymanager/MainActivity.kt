package com.example.librarymanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.librarymanager.databinding.ActivityMainBinding
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
        mainBinding.activity = this
        adapter = BookAdapter()
        mainBinding.adapter = adapter
        BookCenter.getInstance().mainActivity = this
        mainBinding.fab.setOnClickListener {
            BookCenter.getInstance().addNewBook()
        }
        mainBinding.btSearch.setOnClickListener {
            viewModel.searchViewContent.value?.run { BookCenter.getInstance().showBooks(this) }
        }
        viewModel.searchViewContent.observe(this) {
            BookCenter.getInstance().showBooks(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BookCenter.getInstance().mainActivity = null
    }
}