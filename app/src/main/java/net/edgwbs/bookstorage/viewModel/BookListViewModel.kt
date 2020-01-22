package net.edgwbs.bookstorage.viewModel

import android.app.Application
import android.app.SharedElementCallback
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.BookRepository
import kotlin.concurrent.thread

class BookListViewModel(application: Application): AndroidViewModel(application) {
    private val repository:BookRepository = BookRepository.instance
    private var bookListLiveData: MutableLiveData<List<Book>> = MutableLiveData()
    private var cachedBookList: MutableList<Book> = mutableListOf()

    private val perPage: Int = 20

    var page: Int = 1
    var totalCount: Int = 0

    init {
        clearCachedBook()
        //loadBookList(1, null)
    }

    fun clearCachedBook() {
        cachedBookList = mutableListOf()
    }

    fun getLiveData(): MutableLiveData<List<Book>> = bookListLiveData

    fun nextLoadBookList(state: String?, requestCallback: RequestCallback) {
        if (perPage * page < totalCount) {
            page += 1
            loadBookList(page, state, requestCallback)
        }
    }

    fun loadBookList(page: Int, state: String?, requestCallback: RequestCallback): Job {
        return viewModelScope.launch {
            kotlin.runCatching {
                val request = repository.getBooks(page, perPage, state)
                Log.d("uuuuuuuuuuu", request.toString())
                if (request.isSuccessful) {
                    request.body()?.content?.let {
                        totalCount = it.totalCount
                        cachedBookList.addAll(it.books)
                        bookListLiveData.postValue(cachedBookList.toList())
                    }
                    null
                } else{
                    request.errorBody()
                }
            }.onSuccess {
                if (it == null){
                    requestCallback.onRequestSuccess()
                } else {
                    Log.d("error", "loadBookList: request fail:$it")
                    requestCallback.onRequestFail()
                }
            }.onFailure {
                Log.d("exception", "loadBookList: fail:$it")
                requestCallback.onFail()
            }.also {
                requestCallback.onFinal()
            }
        }
    }
}