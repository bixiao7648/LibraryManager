package com.example.librarymanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.librarymanager.databinding.ActivityMainBinding
import com.example.librarymanager.recyclerview.BookAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    var adapter: BookAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.activity = this
        adapter = BookAdapter()
        mainBinding.adapter = adapter
        BookCenter.getInstance().mainActivity = this
    }

    fun addNewBook() {
        BookCenter.getInstance().addNewBook()
    }

    fun showBooks() {
        val keyword = mainBinding.searchViewContent ?: ""
        BookCenter.getInstance().showBooks(keyword)
    }

    override fun onStart() {
        super.onStart()
        BookCenter.getInstance().showBooks("")
    }

    override fun onDestroy() {
        super.onDestroy()
        BookCenter.getInstance().mainActivity = null
    }
}