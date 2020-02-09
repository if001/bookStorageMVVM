package net.edgwbs.bookstorage.viewModel

import android.app.Application
import android.app.SharedElementCallback
import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.room.Room
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.*
import net.edgwbs.bookstorage.model.db.BookSchema
import net.edgwbs.bookstorage.model.db.BooksDB
import net.edgwbs.bookstorage.model.db.BooksDao
import net.edgwbs.bookstorage.utils.ErrorFeedback
import java.lang.Error
import java.lang.Exception
import kotlin.concurrent.thread

class BookListViewModel(application: Application): AndroidViewModel(application) {
    private val repository:BookRepository = BookRepository.instance
    private val perPage: Int = 5
    // var page: Int = 1

    private lateinit var bookPagedList: LiveData<PagedList<Book>>
    // private var bookLiveDataSource: MutableLiveData<PageKeyedDataSource<Int, Book>>? = null
    val networkState = MutableLiveData<NetworkState>()
    private lateinit var bookDataSourceFactory: BookDataSourceFactory

    fun createDataSource(booksDB: BooksDB, errorFeedbackHandler: MutableLiveData<ErrorFeedback>) {
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(perPage)
            .setInitialLoadSizeHint(perPage)
            .build()

//        bookDataSourceFactory = BookDataSourceFactory(booksDB)
//        val bookBoundaryCallback = BookBoundaryCallback(viewModelScope, booksDB, networkState, perPage, errorFeedbackHandler)
//        bookPagedList = LivePagedListBuilder(bookDataSourceFactory, pagedListConfig)
//            .setBoundaryCallback(bookBoundaryCallback)
//            .build()

        bookDataSourceFactory = BookDataSourceFactory(booksDB)
        val bookBoundaryCallback = BookBoundaryCallback(viewModelScope, booksDB, networkState, perPage, errorFeedbackHandler)
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
    }

    fun cancelJob() {
        viewModelScope.cancel()
    }
}
