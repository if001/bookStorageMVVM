package net.edgwbs.bookstorage.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.*
import net.edgwbs.bookstorage.model.*
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory
import net.edgwbs.bookstorage.utils.ErrorFeedback

class BookListViewModel(application: Application): AndroidViewModel(application) {
    private val perPage: Int = 10
    // todo コネクションプールどうなってんの??????
    private val booksRepository: BookRepositoryFactory = BookRepositoryFactory.getInstance(application)

    private lateinit var bookPagedList: LiveData<PagedList<Book>>
    private lateinit var bookDataSourceFactory: BookDataSourceFactory
    private lateinit var bookBoundaryCallback: BookBoundaryCallback


    fun createDataSource(errorFeedbackHandler: MutableLiveData<ErrorFeedback>,
                         loadState: MutableLiveData<LoadState>) {
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(perPage)
            .setInitialLoadSizeHint(perPage)
            .build()

        bookDataSourceFactory = BookDataSourceFactory(booksRepository)
        bookBoundaryCallback = BookBoundaryCallback(
            viewModelScope,
            booksRepository,
            perPage,
            errorFeedbackHandler,
            loadState
        )
        bookPagedList = LivePagedListBuilder(bookDataSourceFactory, pagedListConfig)
            .setBoundaryCallback(bookBoundaryCallback)
            .build()
    }

    fun getLiveData(): LiveData<PagedList<Book>> = bookPagedList

    fun changeState(book: Book,
                    errorFeedbackHandler: MutableLiveData<ErrorFeedback>,
                    loadState: MutableLiveData<LoadState>) {
        return BookModelCommon.changeState(book, viewModelScope,
            booksRepository, errorFeedbackHandler, loadState)
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
