package net.edgwbs.bookstorage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookRegisterBinding
import net.edgwbs.bookstorage.viewModel.BookListViewModel


class BookRegisterFragment : Fragment() {
//    private val viewModel: BookListViewModel by lazy {
//        ViewModelProviders.of(this).get(BookListViewModel::class.java)
//    }
    private lateinit var binding: FragmentBookRegisterBinding
    // private lateinit var adapter: BookListAdapter
    private var page: Int = 1
    private var state: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_book_register, container, false)

        binding.isLoading = true
        return binding.root
    }
}