package net.edgwbs.bookstorage.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookDetailBinding
import net.edgwbs.bookstorage.model.BookResponse
import net.edgwbs.bookstorage.model.HandelError
import net.edgwbs.bookstorage.utils.ErrorFeedback
import net.edgwbs.bookstorage.utils.FragmentConstBookID
import net.edgwbs.bookstorage.viewModel.BookViewModel
import net.edgwbs.bookstorage.viewModel.RequestCallback
import org.xml.sax.ErrorHandler

class BookDetailFragment : Fragment() {
    private val viewModel: BookViewModel by lazy {
        ViewModelProviders.of(this).get(BookViewModel::class.java)
    }
    private lateinit var binding: FragmentBookDetailBinding
    private var bookIDLong: Long? = null
    private val errorFeedbackHandler = MutableLiveData<ErrorFeedback>()
    private val loadState: MutableLiveData<LoadState> = MutableLiveData()
    private val bookStateLoadState: MutableLiveData<LoadState> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        val bookID = bundle!!.getString(FragmentConstBookID)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_detail, container, false)

        binding.isLoading = true
        binding.isStateChangeLoading = true

        binding.backButton.setOnClickListener {
            toPrevPage()
        }

        bookIDLong = bookID?.toLongOrNull()
        if (bookIDLong != null) {
            Log.d("tag", "id!!!!!!!!!!!!!," + bookID)
            viewModel.loadBook(bookIDLong!!, errorFeedbackHandler, loadState, bookStateLoadState)
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
                        binding.bookStateIcon.setOnClickListener{
                            binding.isStateChangeLoading = true
                            viewModel.changeState(book, errorFeedbackHandler, bookStateLoadState)
                        }
                        binding.book = book
                    }
                })
        }
        loadState.observe(
            viewLifecycleOwner,
            Observer {
                binding.isLoading = when(it){
                    LoadState.Loaded -> false
                    LoadState.Loading -> true
                    else -> false
                }
            }
        )
        bookStateLoadState.observe(
            viewLifecycleOwner,
            Observer {
                binding.isStateChangeLoading = when(it){
                    LoadState.Loaded -> false
                    LoadState.Loading -> true
                    else -> false
                }
            }
        )
        errorFeedbackHandler.observe(
            viewLifecycleOwner,
            Observer { feedback ->
                // Log.d("tag", n.getMessage(context).toString())
                val snackbarTime = when(feedback) {
                    ErrorFeedback.ApiNotReachErrorFeedback -> {
                        Snackbar.LENGTH_INDEFINITE
                    }
                    else -> Snackbar.LENGTH_LONG
                }
                feedback?.let{
                    Snackbar.make(binding.root, it.getMessage(), snackbarTime).show()
                }
            }
        )
    }

    private fun toPrevPage() {
        viewModel.cancelJob()
        fragmentManager?.popBackStack()
    }
}


enum class LoadState{
    Loading,
    Loaded
}