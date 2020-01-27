package net.edgwbs.bookstorage.view

import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookCardBinding
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.utils.VIEW_TYPE_EMPTY_ITEM
import net.edgwbs.bookstorage.utils.VIEW_TYPE_ITEM
import net.edgwbs.bookstorage.utils.isCurrent


class BookListAdapter(
    private val bookClickCallback: BookClickCallback
) : PagedListAdapter<Book, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    class BookViewHolder(var binding: FragmentBookCardBinding) : RecyclerView.ViewHolder(binding.root)
    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding: FragmentBookCardBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_book_card, parent, false)
            binding.callback = bookClickCallback
            BookViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_book_card_empty, parent, false)
            EmptyViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_ITEM) {
            val h = holder as BookViewHolder
            h.binding.book = getItem(position)
            h.binding.executePendingBindings()
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            getItem(position) == Book.forEmpty() -> VIEW_TYPE_EMPTY_ITEM
            else -> VIEW_TYPE_ITEM
        }
    }

    fun getItemByPosition(position: Int): Book? {
        return getItem(position)

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }
        }
    }
}

interface BookClickCallback {
    fun onClick(book: Book)
}
