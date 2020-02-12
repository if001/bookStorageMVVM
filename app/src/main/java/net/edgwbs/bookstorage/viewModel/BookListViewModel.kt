package net.edgwbs.bookstorage.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.*
import net.edgwbs.bookstorage.model.*
import net.edgwbs.bookstorage.model.db.BooksDB
import net.edgwbs.bookstorage.utils.ApiNotReachException
import net.edgwbs.bookstorage.utils.BadRequestException
import net.edgwbs.bookstorage.utils.ErrorFeedback

class BookListViewModel(application: Application): AndroidViewModel(application) {
    private val repository:BookRepository = BookRepository.instance
    private val perPage: Int = 10

    private lateinit var bookPagedList: LiveData<PagedList<Book>>
    // private var bookLiveDataSource: MutableLiveData<PageKeyedDataSource<Int, Book>>? = null
    val networkState = MutableLiveData<NetworkState>()
    private lateinit var bookDataSourceFactory: BookDataSourceFactory
    private lateinit var bookBoundaryCallback: BookBoundaryCallback

    fun createDataSource(booksDB: BooksDB, errorFeedbackHandler: MutableLiveData<ErrorFeedback>) {
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(perPage)
            .setInitialLoadSizeHint(perPage)
            .build()

        bookDataSourceFactory = BookDataSourceFactory(booksDB)
        bookBoundaryCallback = BookBoundaryCallback(
            viewModelScope,
            booksDB,
            networkState,
            perPage,
            errorFeedbackHandler
        )

        bookPagedList = LivePagedListBuilder(bookDataSourceFactory, pagedListConfig)
            .setBoundaryCallback(bookBoundaryCallback)
            .build()
    }

    fun getLiveData(): LiveData<PagedList<Book>> = bookPagedList

    fun changeState(book: Book, requestCallback: RequestCallback): Job {
        return BookModelCommon.changeState(book, viewModelScope, repository, requestCallback)
    }

    fun refreshData() {
        bookDataSourceFactory.source?.invalidate()
    }

    fun changeQuery(query: BookListQuery) {
        bookDataSourceFactory.changeQuery(query)
        bookBoundaryCallback.changeQuery(query)
    }

    fun cancelJob() {
        viewModelScope.cancel()
    }
}
