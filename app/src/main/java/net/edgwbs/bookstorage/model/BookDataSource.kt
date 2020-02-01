package net.edgwbs.bookstorage.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.*
import net.edgwbs.bookstorage.viewModel.RequestCallback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class BookDataSource(private val scope: CoroutineScope, private val perPage: Int,
                     private val state: ReadState?,
                     private val networkState: MutableLiveData<NetworkState>):
    PageKeyedDataSource<Int, Book>() {

    private val repository:BookRepository = BookRepository.instance
    private val firstPage = 1

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Book>) {
        callAPI(firstPage, perPage) { books, hasMore ->
            Log.d("tag", "init!!!!!!!!!")
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

    private fun callAPI(page: Int, perPage: Int, callback: (books: List<Book>, hasMore: Boolean) -> Unit) {
        networkState.postValue(NetworkState.RUNNING)
        scope.launch {
            kotlin.runCatching {
                Log.d("tag", "launch!!!!!!!!!")
                val stateQuery = state?.let { getReadStateStr(state) }
                val request = repository.getBooks(page, perPage, stateQuery)
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
                Log.d("tag", "success!!!!!!!!!!1")
                networkState.postValue(state)
            }.onFailure {
                Log.d("tag", it.toString())
                Log.d("tag", "fail!!!!!!!!")
                networkState.postValue(NetworkState.FAILED)
            }.also {
                Log.d("tag", "also!!!!!!!!!")
                networkState.postValue(NetworkState.NOTWORK)
            }
        }
    }

}


class BookDataSourceFactory(private val scope: CoroutineScope,
                            private val perPage: Int,
                            private val networkState: MutableLiveData<NetworkState>)
    :DataSource.Factory<Int, Book>() {


    val bookLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Book>>()
    var source: DataSource<Int, Book>? = null
    var state: ReadState? = null

    override fun create(): DataSource<Int, Book> {
        source = BookDataSource(scope, perPage, state, networkState)
        bookLiveDataSource.postValue(source as? PageKeyedDataSource<Int, Book>)
        return source!!
    }

    fun changeState(state: ReadState?) {
        this.state = state
    }
}


enum class NetworkState {
    NOTWORK,
    RUNNING,
    SUCCESS,
    FAILED
}