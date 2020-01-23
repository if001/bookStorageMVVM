package net.edgwbs.bookstorage.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookRegisterBinding
import net.edgwbs.bookstorage.databinding.FragmentSearchResultCardBinding
import net.edgwbs.bookstorage.model.BookResult
import net.edgwbs.bookstorage.model.SearchResult
import net.edgwbs.bookstorage.viewModel.SearchResultBookListViewModel

class RakutenBookListAdapter(
    val checkBoxCallback: () -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class RakutenBookViewHolder(var binding: FragmentSearchResultCardBinding): RecyclerView.ViewHolder(binding.root)

    private var bookList: MutableList<BookResult>? = null

    // TODO isbnでidentifyとってるのでisbnを持たない本が存在すると死ぬ
    private var checkedList: MutableList<BookResult?> = mutableListOf()
    fun getCheckedList(): MutableList<BookResult?> = checkedList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: FragmentSearchResultCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fragment_search_result_card, parent,false)
        return RakutenBookViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val h = holder as RakutenBookViewHolder
        h.binding.book = bookList?.get(position)

        h.binding.registerCheckbox.setOnClickListener {
            val beforeCheckBoxValue = h.binding.registerCheckbox.isChecked
            if (beforeCheckBoxValue) {
                if (checkedList.size >= 5) {
                    h.binding.registerCheckbox.isChecked = false
                    val t = Toast.makeText(
                        h.binding.root.context,
                        "同時に登録できるのは5つまでです。",
                        Toast.LENGTH_LONG
                    )
                    t.show()
                } else {
                    checkedList.add(h.binding.book)
                }
            } else {
                checkedList.remove(h.binding.book)
            }
            checkBoxCallback()
        }

        h.binding.executePendingBindings()
    }


    fun setSearchResult(bookList: List<BookResult>) {
        if (this.bookList == null){
            val tmp = bookList.toMutableList()
            this.bookList = tmp
            notifyItemRangeInserted(0, tmp.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return requireNotNull(this@RakutenBookListAdapter.bookList).size
                }

                override fun getNewListSize(): Int {
                    return bookList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldList = this@RakutenBookListAdapter.bookList
                    return oldList?.get(oldItemPosition)?.isbn == bookList[newItemPosition].isbn
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val new = bookList[newItemPosition]
                    val old = bookList[oldItemPosition]
                    return new.isbn == old.isbn
                }
            })

            val tmp = bookList.toMutableList()
            this.bookList = tmp
            result.dispatchUpdatesTo(this)
        }
    }
}


interface SearchResultClickCallback {
    fun onClick(bookResult: BookResult)
}


