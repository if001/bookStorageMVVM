package net.edgwbs.bookstorage.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BookDataSource(private val scope: CoroutineScope, private val perPage: Int) : PageKeyedDataSource<Int, Book>() {
    private val repository:BookRepository = BookRepository.instance
    private val firstPage = 1
    private val networkState = MutableLiveData<NetworkState>()


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Book>) {
        callAPI(firstPage, perPage) { books, hasMore ->
            val key = if (hasMore)  1 else null
            callback.onResult(books,  null, key)
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

    private fun callAPI(page: Int, perPage: Int, callback: (books: List<Book>, hasMore: Boolean) -> Unit) {
        var state = NetworkState.RUNNING
        networkState.postValue(state)
        scope.launch {
            kotlin.runCatching {
                val request = repository.getBooks(page, perPage, null)
                state = if (request.isSuccessful) {
                    request.body()?.content?.let {
                        val hasMore = it.totalCount > perPage * page
                        callback(it.books, hasMore)
                    }
                    NetworkState.SUCCESS
                } else {
                    NetworkState.FAILED
                }
            }.onFailure {
                state = NetworkState.FAILED
            }
            networkState.postValue(state)
        }
    }

    fun getNetworkState(): MutableLiveData<NetworkState> {
        return networkState
    }
}


class BookDataSourceFactory(private val scope: CoroutineScope, private val perPage: Int) : DataSource.Factory<Int, Book>() {
    val bookLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Book>>()
    val source = BookDataSource(scope, perPage)

    override fun create(): DataSource<Int, Book> {
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