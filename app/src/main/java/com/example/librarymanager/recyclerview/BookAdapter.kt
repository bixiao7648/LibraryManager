package com.example.librarymanager.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanager.BookCenter
import com.example.librarymanager.R
import com.example.librarymanager.db.BookInfo

class BookAdapter : RecyclerView.Adapter<BookViewHolder>() {

    private var items: List<BookInfo> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            BookCenter.getInstance().startEditPage(items[position])
        }
        holder.title.text = items[position].title
        holder.author.text = items[position].author
        holder.isbn.text = items[position].isbn
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<BookInfo>) {
        items = newItems
        notifyDataSetChanged()
    }
}