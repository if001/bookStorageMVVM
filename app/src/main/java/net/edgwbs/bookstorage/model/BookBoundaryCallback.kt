package net.edgwbs.bookstorage.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import kotlinx.coroutines.*
import net.edgwbs.bookstorage.model.db.AuthorSchema
import net.edgwbs.bookstorage.model.db.BookSchema
import net.edgwbs.bookstorage.model.db.BooksDB
import net.edgwbs.bookstorage.model.db.PublisherSchema
import net.edgwbs.bookstorage.utils.ApiNotReachException
import net.edgwbs.bookstorage.utils.BadRequestException
import net.edgwbs.bookstorage.utils.ErrorFeedback

class BookBoundaryCallback(
    private val scope: CoroutineScope,
    private val booksDB: BooksDB,
    private val networkState: MutableLiveData<NetworkState>,
    private val perPage: Int,
    private val errorFeedbackHandler: MutableLiveData<ErrorFeedback>
): PagedList.BoundaryCallback<Book>() {
    private val repository:BookRepository = BookRepository.instance
    private val authorsDB = booksDB.authorsDao()
    private val publishersDB = booksDB.publishersDao()

    var nextPage = 1
    var totalCount: Long = -1
    var query = BookListQuery(null, null)

    override fun onZeroItemsLoaded() {
        Log.d("tag", "onZeroItemsLoaded")
        // DBから初回取得時にデータが無いとき
        if (totalCount == (0).toLong())
            return

        scope.launch {
            callApiAsync(1, perPage, query)
                .await()
                .onSuccess {
                    Log.d("tag", it.toString())
                    it?.let{ b -> totalCount = b.content.total_count }
                    kotlin.runCatching {
                        withContext(scope.coroutineContext) {
                            insertAuthorPublisher(it)
                            insertBook(it)
                        }
                    }.onSuccess {
                        nextPage += 1
                    }.onFailure {
                        errorFeedbackHandler.postValue(ErrorFeedback.DatabaseErrorFeedback(it.toString()))
                    }
                }
        }
    }

    override fun onItemAtEndLoaded(bookAtEnd: Book) {
        Log.d("tag", "onItemAtEndLoaded")
        // DBに2ページ以降のデータが無いとき
        // 引数に前回取得したデータの最後のものが渡される
        // 以下の条件ではAPIを呼ばないように早期リターン
        if (totalCount < (nextPage -1) * perPage && totalCount != (-1).toLong())
            return
        if (totalCount < perPage && totalCount != (-1).toLong())
            return
        scope.launch {
            callApiAsync(nextPage, perPage, query)
                .await()
                .onSuccess {
                    it?.let{ b -> totalCount = b.content.total_count }
                    kotlin.runCatching {
                        withContext(scope.coroutineContext) {
                            insertAuthorPublisher(it)
                            insertBook(it)
                            Log.d("tag", nextPage.toString())
                        }
                    }.onSuccess {
                        nextPage += 1
                    }.onFailure {
                        errorFeedbackHandler.postValue(ErrorFeedback.DatabaseErrorFeedback(it.toString()))
                    }
                }
        }
    }

    private fun insertAuthorPublisher(response: BookResponse<PaginateBook>?) {
            response?.let {
                it.content.books.forEach { book ->
                    book.author?.let { ele ->
                        scope.launch(Dispatchers.IO) {
                            authorsDB.find(ele.id) ?: {
                                scope.launch(Dispatchers.IO) {
                                    val new = AuthorSchema(ele.id, ele.name)
                                    authorsDB.insert(listOf(new))
                                }
                            }()
                        }
                    }
                    book.publisher?.let { ele ->
                        scope.launch(Dispatchers.IO) {
                            publishersDB.find(ele.id) ?: {
                                scope.launch(Dispatchers.IO) {
                                    val new = PublisherSchema(ele.id, ele.name)
                                    publishersDB.insert(listOf(new))
                                }
                            }()
                        }
                    }
                }
            }
    }


    private fun insertBook(response: BookResponse<PaginateBook>?) {
        response?.let {
            val b = it.content.books.map{ book ->
                BookSchema(
                    book.id,
                    book.accountId,
                    book.title,
                    book.author?.id,
                    book.publisher?.id,
                    book.isbn,
                    book.smallImageUrl,
                    book.mediumImageUrl,
                    book.readState,
                    book.startAt,
                    book.endAt,
                    book.AffiliateUrl,
                    book.createdAt,
                    book.updatedAt
                )
            }
            scope.launch(Dispatchers.IO) {
                Log.d("tag", b.toString())
                Log.d("tag", "insert book!!!!!")
                booksDB.booksDao().insert(b)
            }
        }
    }

//    fun clear() {
//        disposable.clear()
//    }


    private suspend fun callApiAsync(page: Int, perPage:Int, query: BookListQuery) = scope.async {
        kotlin.runCatching {
            networkState.postValue(NetworkState.RUNNING)
            val response = repository.getBooks(page, perPage, query.getStateStr(), query.book)
            if (response.isSuccessful) {
                networkState.postValue(NetworkState.SUCCESS)
                response.body()
            } else {
                throw BadRequestException(response.errorBody()?.toString())
            }
        }.onFailure {
            Log.d("tag", it.toString())
            when(it) {
                ApiNotReachException() ->
                    errorFeedbackHandler.postValue(ErrorFeedback.ApiNotReachErrorFeedback(it.toString()))
                else ->
                    errorFeedbackHandler.postValue(ErrorFeedback.ApiErrorFeedback(it.toString(), 500))
            }
            networkState.postValue(NetworkState.FAILED)
        }.also {
            networkState.postValue(NetworkState.NOTWORK)
        }
    }

    fun changeQuery(query: BookListQuery) {
        this.query = query
    }
}
