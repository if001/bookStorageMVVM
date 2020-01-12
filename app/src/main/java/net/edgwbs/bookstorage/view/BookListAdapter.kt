package net.edgwbs.bookstorage.view

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView

import androidx.databinding.BindingAdapter
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.synthetic.main.fragment_book_card.view.*
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookCardBinding
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.ReadState


class BookListAdapter(private val bookClickCall: BookClickCallback) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {
    class BookViewHolder(var binding: FragmentBookCardBinding) : RecyclerView.ViewHolder(binding.root)

    private var bookList: List<BookShowModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding:FragmentBookCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fragment_book_card, parent, false)
        binding.callback = bookClickCall

        return BookViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookList?.size ?: 0
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.binding.book = bookList?.get(position)

//        holder.binding.book?.let {
//            Log.d("eeeee", it.readState.toString())
//            when(it.readState) {
//                ReadState.NotRead.state -> {
//                    Log.d("eeeee", "huhuuuu")
//                    holder.itemView.book_list_icon.setText(R.string.fa_book_solid)
//                }
//                ReadState.Reading.state -> {
//                    Log.d("eeeee", "huhuuuu2")
//                    holder.itemView.book_list_icon.setText(R.string.fa_book_open_solid)
//                }
//                ReadState.Read.state -> {
//                    Log.d("eeeee", "huhuuuu3")
//                    holder.itemView.book_list_icon.setText(R.string.fa_check_solid)
//                }
//            }
//        }
        holder.binding.executePendingBindings()
    }


    fun setBookList(bookList: List<BookShowModel>) {
        if (this.bookList == null){
            this.bookList = bookList
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
    fun onClick(book: BookShowModel)
}

object CustomBindingAdapter {
    @BindingAdapter("app:visibleGone")
    @JvmStatic
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}
