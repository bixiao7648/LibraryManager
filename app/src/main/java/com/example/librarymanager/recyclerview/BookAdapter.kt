package com.example.librarymanager.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanager.R
import com.example.librarymanager.databinding.ItemLayoutBinding
import com.example.librarymanager.db.BookInfo

class BookAdapter(
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (BookInfo) -> Unit
) : RecyclerView.Adapter<BookViewHolder>() {

    private var items: List<BookInfo> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemBinding = DataBindingUtil.inflate<ItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.item_layout, parent, false)
        return BookViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClick.invoke(items[position])
        }
        holder.itemView.setOnLongClickListener {
            items[position].id?.run { onDeleteClick.invoke(this) }
            true
        }
        holder.binding.title = items[position].title
        holder.binding.author = items[position].author
        holder.binding.isbn = items[position].isbn
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<BookInfo>) {
        items = newItems
        notifyDataSetChanged()
    }
}