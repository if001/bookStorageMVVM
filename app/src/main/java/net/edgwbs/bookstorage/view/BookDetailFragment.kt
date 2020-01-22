package net.edgwbs.bookstorage.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_book_register.view.*
import kotlinx.coroutines.Job
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookDetailBinding
import net.edgwbs.bookstorage.utils.FontAwesomeTextView
import net.edgwbs.bookstorage.utils.FragmentConstBookID
import net.edgwbs.bookstorage.viewModel.BookViewModel
import net.edgwbs.bookstorage.viewModel.RequestCallback

class BookDetailFragment : Fragment() {
    private val viewModel: BookViewModel by lazy {
        ViewModelProviders.of(this).get(BookViewModel::class.java)
    }
    private lateinit var binding: FragmentBookDetailBinding
    private lateinit var job: Job

    private val loadBookCallback = object: RequestCallback {
        override fun onRequestSuccess() {
        }

        override fun onRequestFail() {
            toPrevPage()
        }

        override fun onFail() {
            toPrevPage()
        }

        override fun onFinal() {
            Thread.sleep(2000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        val bookID = bundle!!.getString(FragmentConstBookID)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_detail, container, false)

        binding.isLoading = true

        binding.backButton.setOnClickListener {
            toPrevPage()
        }

        val bookIDLong = bookID?.toLongOrNull()
        if (bookIDLong != null) {
            job = viewModel.loadBook(bookIDLong, loadBookCallback)
        } else {
            toPrevPage()
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

    private fun toPrevPage() {
        if(::job.isInitialized) job.cancel()
        fragmentManager?.popBackStack()
    }
}