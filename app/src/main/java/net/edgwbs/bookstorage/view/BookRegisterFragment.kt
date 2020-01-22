package net.edgwbs.bookstorage.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import info.androidhive.fontawesome.FontDrawable
import kotlinx.coroutines.delay
import net.edgwbs.bookstorage.R
import net.edgwbs.bookstorage.databinding.FragmentBookRegisterBinding
import net.edgwbs.bookstorage.viewModel.RequestCallback
import net.edgwbs.bookstorage.viewModel.SearchResultBookListViewModel
import kotlinx.coroutines.*



class BookRegisterFragment : Fragment() {
    private val viewModel: SearchResultBookListViewModel by lazy {
        ViewModelProviders.of(this).get(SearchResultBookListViewModel::class.java)
    }
    private lateinit var binding: FragmentBookRegisterBinding
    private lateinit var adapter: RakutenBookListAdapter
    private var page: Int = 1
    private var state: String? = null

    private val loadRequestCallback: RequestCallback = object : RequestCallback {
        override fun onRequestSuccess() {}
        override fun onRequestFail() {}
        override fun onFail() {}
        override fun onFinal() {
            Thread.sleep(4000)
            closeSearchBox()
            binding.isLoading = false
        }
    }
    private val registerRequestCallback: RequestCallback = object : RequestCallback {
        override fun onRequestSuccess() {
            val fragment = BookListFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.let {
                it.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                it.addToBackStack(null)
                it.replace(R.id.fragment_container, fragment).commit()
            }
        }
        override fun onRequestFail() {
            Snackbar.make(binding.root , "request fail", Snackbar.LENGTH_LONG).show()
        }
        override fun onFail() {
            Snackbar.make(binding.root , "fail", Snackbar.LENGTH_LONG).show()
        }
        override fun onFinal() {
            Thread.sleep(4000)

            binding.addRegisterSubmit.isClickable = true
            binding.addRegisterSubmit.clearAnimation()
            val drawable = FontDrawable(context, R.string.fa_plus_solid, true, true)
            binding.addRegisterSubmit.setImageDrawable(drawable)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_book_register, container, false)

        binding.isLoading = false
        binding.hideSearchBox = false

        binding.searchResultBookList.layoutManager = LinearLayoutManager(context)


        val zoomIn = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
        val zoomOut = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
        adapter = RakutenBookListAdapter {
            val beforeIsZero = !binding.ableRegister
            val afterIsZero = adapter.getCheckedList().size == 0
            binding.ableRegister = !afterIsZero

            if (beforeIsZero && !afterIsZero) {
                binding.addRegisterSubmit.startAnimation(zoomIn)
            }
            if (!beforeIsZero && afterIsZero) {
                binding.addRegisterSubmit.startAnimation(zoomOut)
            }
        }
        binding.searchResultBookList.adapter = adapter

        binding.backButton.setOnClickListener {
            it.setOnClickListener {
                fragmentManager
                    ?.beginTransaction()
                    ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                fragmentManager?.popBackStack()
            }
        }

        binding.ableRegister = false
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeViewModel(viewModel)

        binding.searchButton.setOnClickListener{
            if (binding.title !=null || binding.author != null) {
                binding.isLoading = true
                viewModel.loadBookList(1, binding.title, binding.author, loadRequestCallback)
                closeInputBox()
            }
        }

        binding.searchOpenButton.setOnClickListener{
            if (binding.hideSearchBox){
                openSearchBox()
                closeInputBox()
            } else {
                closeSearchBox()
                closeInputBox()
            }
        }

        val drawable = FontDrawable(context, R.string.fa_plus_solid, true, true)
        context?.let {
            drawable.setTextColor(ContextCompat.getColor(it, android.R.color.white))
        }
        binding.addRegisterSubmit.setImageDrawable(drawable)
        binding.addRegisterSubmit.setOnClickListener {
            val loadingDrawable = FontDrawable(context, R.string.fa_spinner_solid, true, true)
            binding.addRegisterSubmit.setImageDrawable(loadingDrawable)
            val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
            binding.addRegisterSubmit.startAnimation(rotate)
            binding.addRegisterSubmit.isClickable = false

            viewModel.registerBooks(adapter.getCheckedList(), registerRequestCallback)
        }
    }

    private fun closeSearchBox() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out_to_top)
        binding.searchBox?.let {
            it.startAnimation(animation)
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                delay(100)
                binding.hideSearchBox = true
            }
        }
    }

    private fun openSearchBox() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in_to_top)
        binding.searchBox?.let {
            it.startAnimation(animation)
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                delay(250)
                binding.hideSearchBox = false
            }
        }
    }

    private fun closeInputBox() {
        activity?.let {
            val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }
    private fun observeViewModel(viewModel: SearchResultBookListViewModel) {
        //データをSTARTED かRESUMED状態である場合にのみ、アップデートするように、LifecycleOwnerを紐付け、ライフサイクル内にオブザーバを追加
        viewModel.getLiveData().observe(
            viewLifecycleOwner,
            Observer { books ->
                if (books != null) {
                    Log.d("debug", "observe start")
                    adapter.setSearchResult(books)
                    binding.isLoading = false
                }
            })
    }
}