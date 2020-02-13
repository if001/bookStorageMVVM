package net.edgwbs.bookstorage.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.*
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.ReadState
import net.edgwbs.bookstorage.model.getReadStateStr
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory
import net.edgwbs.bookstorage.repositories.db.BooksDB
import net.edgwbs.bookstorage.repositories.api.BookRepository


// deprecated!!!
class BookDataSource2(private val scope: CoroutineScope,
                      private val perPage: Int,
                      private val networkState: MutableLiveData<NetworkState>,
                      private val query: BookListQuery
):
    PageKeyedDataSource<Int, Book>() {

    private val repository: BookRepository = BookRepository.instance
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
                val request = repository.getBooks(page, perPage, query.getStateStr(), query.book)
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

// deprecated!!!
class BookDataSource(private val scope: CoroutineScope,
                     private val booksDB: BooksDB,
                     private val perPage: Int,
                     private val query: BookListQuery
):
    PageKeyedDataSource<Int, Book>() {

    private val repository: BookRepository = BookRepository.instance
    private val firstPage = 1

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Book>) {
        fetchData(firstPage, perPage) { books, hasMore ->
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
        fetchData(params.key, perPage) { books, hasMore ->
            val key = if (hasMore) params.key + 1 else null
            callback.onResult(books,  key)
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Book>) {
        fetchData(params.key, perPage) { books, _ ->
            val key = if (params.key > 1) params.key - 1 else null
            callback.onResult(books,  key)
        }
    }

    private fun fetchData(page: Int, perPage: Int, callback: (books: List<Book>, hasMore: Boolean) -> Unit) {
        scope.launch {
            kotlin.runCatching {
                Log.d("tag", "launch!!!!!!!!!")
                val total = booksDB.booksDao().count()
                val hasMore = total > perPage * page
                val offset = (page - 1) * perPage
                val books = booksDB.booksDao().loadAllWithPaged(perPage, offset).map{ b -> b.toModel()}
                callback(books, hasMore)
            }.onSuccess {
                Log.d("tag", "success!!!!!!!!!!1")
                // networkState.postValue(state)
            }.onFailure {
                Log.d("tag", it.toString())
                Log.d("tag", "fail!!!!!!!!")
                // networkState.postValue(NetworkState.FAILED)
            }.also {
                Log.d("tag", "also!!!!!!!!!")
                // networkState.postValue(NetworkState.NOTWORK)
            }
        }
    }

}



class BookDataSourceFactory(bookRepository: BookRepositoryFactory):DataSource.Factory<Int, Book>() {
    private val booksDB = bookRepository.getDB()
    var query: BookListQuery =
        BookListQuery(null, null)
    var source: DataSource<Int, Book>? = null

    override fun create(): DataSource<Int, Book> {
        val dbQuery = if(query.state != null && query.book != null) {
            booksDB.booksDao().findByBookInfoAndState(query.book!!, query.state!!.value)
        } else if(query.state != null) {
            booksDB.booksDao().findByState(query.state!!.value)
        } else if(query.book != null) {
            booksDB.booksDao().findByBookInfo(query.book!!)
        } else {
            booksDB.booksDao().loadAll()
        }
        Log.d("tag:", query.book.toString())
        val bookDataSourceFactory = dbQuery.map { booksSchema ->
            booksSchema.toModel()
        }
        source = bookDataSourceFactory.create()
        return source!!
    }

    fun changeQuery(query: BookListQuery) {
        this.query = query
    }
}


class BookDataSourceFactory2(
    private val booksDB: BooksDB,
    private val scope: CoroutineScope,
    private val perPage: Int): DataSource.Factory<Int, Book>() {

    val bookLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Book>>()
    var source: DataSource<Int, Book>? = null
    var query: BookListQuery =
        BookListQuery(null, null)

    override fun create(): DataSource<Int, Book> {
        source = BookDataSource(scope, booksDB, perPage, query)
        bookLiveDataSource.postValue(source as? PageKeyedDataSource<Int, Book>)
        return source!!
    }

    fun changeQuery(query: BookListQuery) {
        this.query = query
    }
}


enum class NetworkState {
    NOTWORK,
    RUNNING,
    SUCCESS,
    FAILED
}


// todo valにする
class BookListQuery(
    var state: ReadState?,
    var book: String?
) {
    fun getStateStr(): String? {
        return this.state?.let { getReadStateStr(it) }
    }
}