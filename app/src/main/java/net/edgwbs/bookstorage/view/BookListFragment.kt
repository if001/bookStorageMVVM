package net.edgwbs.bookstorage.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookListBinding
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.viewModel.BookListViewModel

class BookListFragment : Fragment() {
    private val viewModel:BookListViewModel by lazy {
        ViewModelProviders.of(this).get(BookListViewModel::class.java)
    }
    private lateinit var binding: FragmentBookListBinding
    private lateinit var adapter: BookListAdapter

    private val bookClickCallback = object: BookClickCallback {
        override fun onClick(book: Book) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_list, container, false)

        Log.d("tag", "create fragment!!!")
        adapter = BookListAdapter(bookClickCallback)

        binding.apply {
            bookList.adapter = adapter
            isLoading = true
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeViewModel(viewModel)
    }

    private fun observeViewModel(viewModel: BookListViewModel) {
        //データをSTARTED かRESUMED状態である場合にのみ、アップデートするように、LifecycleOwnerを紐付け、ライフサイクル内にオブザーバを追加
        viewModel.getLiveData().observe(
            viewLifecycleOwner,
            Observer { books ->
                Log.d("tag", books.toString())
                if (books != null) {
                    adapter.setBookList(books)
                    binding.isLoading = false
                }
            })
    }
}
