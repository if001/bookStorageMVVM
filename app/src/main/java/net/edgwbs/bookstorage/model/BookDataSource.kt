package net.edgwbs.bookstorage.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.viewModel.RequestCallback
import retrofit2.Response

class BookDataSource(private val scope: CoroutineScope, private val perPage: Int) : PageKeyedDataSource<Int, Book>() {
    private val repository:BookRepository = BookRepository.instance
    private val firstPage = 1
    private val networkState = MutableLiveData<NetworkState>()
    // private val stateQuery: String? = state?.let { getReadStateStr(it) }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Book>) {
        callAPI(firstPage, perPage) { books, hasMore ->
            val key = if (hasMore) 1 else null
            val bookWithEmpty = mutableListOf(Book.forEmpty())
            bookWithEmpty.addAll(books)
            callback.onResult(bookWithEmpty,  null, key)
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Book>) {
        callAPI(params.key, perPage) { books, hasMore ->
            val key = if (hasMore) params.key + 1 else null
            callback.onResult(books,  key)
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Book>) {
        callAPI(params.key, perPage) { books, _ ->
            val key = if (params.key > 1) params.key - 1 else null
            callback.onResult(books,  key)
        }
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }

    fun refreshData() {
        super.invalidate()
    }

    private fun callAPI(page: Int, perPage: Int, callback: (books: List<Book>, hasMore: Boolean) -> Unit) {
        networkState.postValue(NetworkState.RUNNING)

        scope.launch {
            kotlin.runCatching {
                val request = repository.getBooks(page, perPage, null)
                if (request.isSuccessful) {
                    request.body()?.content?.let {
                        val hasMore = it.total_count > perPage * page
                        callback(it.books, hasMore)
                    }
                    NetworkState.SUCCESS
                } else {
                    NetworkState.FAILED
                }
            }.onSuccess { state ->
                networkState.postValue(state)
            }.onFailure {
                networkState.postValue(NetworkState.FAILED)
            }.also {
                networkState.postValue(NetworkState.NOTWORK)
            }
        }
    }

    fun getNetworkState(): MutableLiveData<NetworkState> {
        return networkState
    }

    fun removeData(data: Book) {

    }
}


class BookDataSourceFactory(private val scope: CoroutineScope, private val perPage: Int) : DataSource.Factory<Int, Book>() {
    val bookLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Book>>()
    val source = BookDataSource(scope, perPage)

    override fun create(): DataSource<Int, Book> {
        Log.d("eee","factory!!!!!!!!!!!")
        bookLiveDataSource.postValue(source)
        return source
    }
}


enum class NetworkState {
    NOTWORK,
    RUNNING,
    SUCCESS,
    FAILED
}