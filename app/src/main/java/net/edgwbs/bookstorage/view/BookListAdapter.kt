package net.edgwbs.bookstorage.view

import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookCardBinding
import net.edgwbs.bookstorage.databinding.MoreLoadButtonBinding
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.utils.VIEW_TYPE_EMPTY_ITEM
import net.edgwbs.bookstorage.utils.VIEW_TYPE_ITEM
import net.edgwbs.bookstorage.utils.VIEW_TYPE_LOADING
import kotlin.coroutines.CoroutineContext


class BookListAdapter(
    private val bookClickCallback: BookClickCallback,
    private val moreLoadButtonCallback: MoreLoadButtonCallback
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class BookViewHolder(var binding: FragmentBookCardBinding) : RecyclerView.ViewHolder(binding.root)
    class LoadingViewHolder(var binding: MoreLoadButtonBinding) : RecyclerView.ViewHolder(binding.root)
    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var bookList: MutableList<Book>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding: FragmentBookCardBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_book_card, parent, false)
            binding.callback = bookClickCallback
            BookViewHolder(binding)
        } else if (viewType == VIEW_TYPE_LOADING) {
            val binding: MoreLoadButtonBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.more_load_button, parent, false)
            binding.isLoading = false
            binding.callback = moreLoadButtonCallback
            LoadingViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_book_card_empty, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return bookList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_ITEM) {
            val h = holder as BookViewHolder
            h.binding.book = bookList?.get(position)
            h.binding.executePendingBindings()
        } else if (holder.itemViewType == VIEW_TYPE_LOADING) {
            val h = holder as LoadingViewHolder
            h.binding.executePendingBindings()
        }
    }


    fun setBookList(bookList: List<Book>) {
        if (this.bookList == null){
            val tmp = bookList.toMutableList()
            tmp.add(0, Book.forEmpty())
            tmp.add(Book.forMoreLoad())
            this.bookList = tmp
            notifyItemRangeInserted(0, tmp.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return requireNotNull(this@BookListAdapter.bookList).size
                }

                override fun getNewListSize(): Int {
                    return bookList.size
                }

                override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                    ): Boolean {
                    val oldList = this@BookListAdapter.bookList
                    return oldList?.get(oldItemPosition)?.id == bookList[newItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return if (bookList.size <= newItemPosition || bookList.size <= oldItemPosition) {
                        // todo this "if statement" may be unnecessary
                        true
                    } else {
                        val new = bookList[newItemPosition]
                        val old = bookList[oldItemPosition]
                        new.id == old.id
                    }
                }
            })
            val tmp = bookList.toMutableList()
            this.bookList = tmp
            result.dispatchUpdatesTo(this)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            bookList == null -> VIEW_TYPE_LOADING
            bookList?.get(position) == Book.forMoreLoad() -> VIEW_TYPE_LOADING
            bookList?.get(position) == Book.forEmpty() -> VIEW_TYPE_EMPTY_ITEM
            else -> VIEW_TYPE_ITEM
        }
    }

    fun addLoadingView() {
        val nullBook = Book.forMoreLoad()
        bookList?.let {
            it.add(nullBook)
            Handler().post {
                notifyItemInserted(it.size - 1)
            }
        }
    }

    fun removeLoadingView() {
        bookList?.let { books ->
            if (books.isNotEmpty()) {
//                val nullBook = books.find { it.id == (-1).toLong() }
//                val index = books.indexOf(nullBook)
//                books.removeAt(index)

                books.removeAt(books.size -1)
                Handler().post {
                    notifyItemRemoved(books.size)
                }
            }
        }
    }

}

interface BookClickCallback {
    fun onClick(book: Book)
}

interface MoreLoadButtonCallback {
    fun onClick()
}
