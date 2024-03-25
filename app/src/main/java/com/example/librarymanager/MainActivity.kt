package com.example.librarymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanager.recyclerview.BookAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.books_recycler_view)
        recyclerView.adapter = BookAdapter(listOf("item 1", "item 2", "item 3"))
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}