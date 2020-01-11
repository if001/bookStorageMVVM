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
import net.edgwbs.bookstorage.databinding.FragmentBookDetailBinding
import net.edgwbs.bookstorage.utils.FontAwesomeTextView
import net.edgwbs.bookstorage.utils.FragmentConstBookID
import net.edgwbs.bookstorage.viewModel.BookViewModel

class BookDetailFragment : Fragment() {
    private val viewModel: BookViewModel by lazy {
        ViewModelProviders.of(this).get(BookViewModel::class.java)
    }
    private lateinit var binding: FragmentBookDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        val bookID = bundle!!.getString(FragmentConstBookID)
        view?.findViewById<FontAwesomeTextView>(R.id.back_button)?.let {
            it.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }
        
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_detail, container, false)

        binding.isLoading = true
        bookID?.toLongOrNull()?.let {
            viewModel.loadBook(it)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeViewModel(viewModel)
    }

    private fun observeViewModel(viewModel: BookViewModel) {
        //データをSTARTED かRESUMED状態である場合にのみ、アップデートするように、LifecycleOwnerを紐付け、ライフサイクル内にオブザーバを追加
        viewModel.run {
            getLiveData().observe(
                viewLifecycleOwner,
                Observer { book ->
                    if (book != null) {
                        binding.isLoading = false
                        binding.book = book
                    }
                })
        }
    }

}