package com.example.librarymanager.recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanager.R

class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.item_title)
    val author: TextView = itemView.findViewById(R.id.item_author)
    val isbn: TextView = itemView.findViewById(R.id.item_isbn)
}