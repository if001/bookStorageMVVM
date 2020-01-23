package net.edgwbs.bookstorage.viewModel

import android.app.Application
import android.app.SharedElementCallback
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.*
import java.lang.Error
import java.lang.Exception
import kotlin.concurrent.thread

class BookListViewModel(application: Application): AndroidViewModel(application) {
    private val repository:BookRepository = BookRepository.instance
    private var bookListLiveData: MutableLiveData<List<Book>> = MutableLiveData()
    private var cachedBookList: MutableList<Book> = mutableListOf()


    private val perPage: Int = 30

    var page: Int = 1


    private val bookPagedList: LiveData<PagedList<Book>>
    private var bookLiveDataSource: MutableLiveData<PageKeyedDataSource<Int, Book>>? = null

    val networkState: LiveData<NetworkState>
    init {
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(perPage)
            .setInitialLoadSizeHint(perPage)
            .build()
        val bookDataSourceFactory = BookDataSourceFactory(viewModelScope, perPage)

        bookLiveDataSource = bookDataSourceFactory.bookLiveDataSource

        networkState = bookDataSourceFactory.source.getNetworkState()

        bookPagedList = LivePagedListBuilder(bookDataSourceFactory, pagedListConfig).build()
    }
    fun getLiveData(): LiveData<PagedList<Book>> = bookPagedList

    fun changeState(book: Book, requestCallback: RequestCallback): Job {
        return BookModelCommon.changeState(book, viewModelScope, repository, requestCallback)
    }

    fun clearCachedBook() {
        cachedBookList = mutableListOf()
    }


    fun loadBookList(page: Int, state: String?, requestCallback: RequestCallback): Job {
        return viewModelScope.launch {
            kotlin.runCatching {
                val request = repository.getBooks(page, perPage, state)
                Log.d("uuuuuuuuuuu", request.toString())
                if (request.isSuccessful) {
                    request.body()?.content?.let {
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