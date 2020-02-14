package net.edgwbs.bookstorage.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.*
import net.edgwbs.bookstorage.di.BookRepositoryComponent
import net.edgwbs.bookstorage.di.BookRepositoryModule
import net.edgwbs.bookstorage.model.*
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory
import net.edgwbs.bookstorage.repositories.db.BooksDB
import net.edgwbs.bookstorage.repositories.api.BookRepository
import net.edgwbs.bookstorage.utils.ErrorFeedback
import net.edgwbs.bookstorage.view.LoadState
import javax.inject.Inject

class BookListViewModel @Inject constructor(application: Application): AndroidViewModel(application) {
    private val perPage: Int = 10
    // private val booksRepository: BookRepositoryFactory = BookRepositoryFactory.getInstance(application)
    @Inject lateinit var booksRepository: BookRepositoryFactory
    private val component = BookRepositoryComponent.builder()
        .repositoryModule(BookRepositoryModule())
        .build()

    init {
        this.component.inject(this)
    }

    private lateinit var bookPagedList: LiveData<PagedList<Book>>
    // todo loadingstateに変更する
    val networkState = MutableLiveData<NetworkState>()
    private lateinit var bookDataSourceFactory: BookDataSourceFactory
    private lateinit var bookBoundaryCallback: BookBoundaryCallback


    fun createDataSource(errorFeedbackHandler: MutableLiveData<ErrorFeedback>) {
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(perPage)
            .setInitialLoadSizeHint(perPage)
            .build()

        bookDataSourceFactory = BookDataSourceFactory(booksRepository)
        bookBoundaryCallback = BookBoundaryCallback(
            viewModelScope,
            booksRepository,
            networkState,
            perPage,
            errorFeedbackHandler
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
