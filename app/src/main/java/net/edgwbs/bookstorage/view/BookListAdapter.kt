package net.edgwbs.bookstorage.view

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView

import androidx.databinding.BindingAdapter
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookCardBinding
import net.edgwbs.bookstorage.model.Book


class BookListAdapter(private val bookClickCall: BookClickCallback) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {
    class BookViewHolder(var binding: FragmentBookCardBinding) : RecyclerView.ViewHolder(binding.root)

    private var bookList: List<Book>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding:FragmentBookCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fragment_book_card, parent, false)
        Log.d("tag", "create!!!!!!")
        binding.callback = bookClickCall

        return BookViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookList?.size ?: 0
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.binding.book = bookList?.get(position)
        holder.binding.executePendingBindings()
    }


    fun setBookList(bookList: List<Book>) {
        Log.d("tag", "set book list")
        if (this.bookList == null){
            notifyItemRangeInserted(0, bookList.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return requireNotNull(this@BookListAdapter.bookList).size
                }

                override fun getNewListSize(): Int {
                    return bookList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldList = this@BookListAdapter.bookList
                    return oldList?.get(oldItemPosition)?.id == bookList[newItemPosition].id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val new = bookList[newItemPosition]
                    val old = bookList[oldItemPosition]
                    return new.id == old.id
                }
            })
            this.bookList = bookList
            result.dispatchUpdatesTo(this)
        }
    }
}

interface BookClickCallback {
    fun onClick(book: Book)
}

object CustomBindingAdapter {
    @BindingAdapter("app:visibleGone")
    @JvmStatic
    fun showHide(view: View, show: Boolean) {
        Log.d("tag","showHide")
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}
