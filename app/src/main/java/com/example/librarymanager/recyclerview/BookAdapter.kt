package com.example.librarymanager.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanager.R

class BookAdapter(private val items: List<String>) : RecyclerView.Adapter<BookViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.title.text = items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }
}