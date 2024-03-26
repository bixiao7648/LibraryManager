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
    var adapter: BookAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = BookAdapter()
        BookCenter.getInstance().mainActivity = this
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            BookCenter.getInstance().addNewBook()
        }
        findViewById<Button>(R.id.bt_search).setOnClickListener {
            BookCenter.getInstance().showBooks()
        }
        recyclerView = findViewById(R.id.books_recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        BookCenter.getInstance().showBooks()
    }

    override fun onDestroy() {
        super.onDestroy()
        BookCenter.getInstance().mainActivity = null
    }
}