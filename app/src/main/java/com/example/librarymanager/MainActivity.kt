package com.example.librarymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanager.recyclerview.BookAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.books_recycler_view)
        val adapter = BookAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        BookCenter.getInstance().showBooks(adapter)
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            BookCenter.getInstance().addNewBook(this)
        }
        findViewById<Button>(R.id.bt_search).setOnClickListener {
            BookCenter.getInstance().showBooks(adapter)
        }
    }
}