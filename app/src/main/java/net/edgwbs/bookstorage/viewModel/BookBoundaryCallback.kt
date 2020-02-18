package net.edgwbs.bookstorage.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import kotlinx.coroutines.*
import net.edgwbs.bookstorage.model.*
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory
import net.edgwbs.bookstorage.repositories.db.AuthorSchema
import net.edgwbs.bookstorage.repositories.db.BookSchema
import net.edgwbs.bookstorage.repositories.db.PublisherSchema
import net.edgwbs.bookstorage.utils.ApiNotReachException
import net.edgwbs.bookstorage.utils.BadRequestException
import net.edgwbs.bookstorage.utils.ErrorFeedback
import java.net.ConnectException

class BookBoundaryCallback(
    private val scope: CoroutineScope,
    bookRepositoryFactory: BookRepositoryFactory,
    private val perPage: Int,
    private val errorFeedbackHandler: MutableLiveData<ErrorFeedback>,
    private val loadState: MutableLiveData<LoadState>
): PagedList.BoundaryCallback<Book>() {
    private val booksDB = bookRepositoryFactory.db.booksDao()
    private val authorsDB = bookRepositoryFactory.db.authorsDao()
    private val publishersDB = bookRepositoryFactory.db.publishersDao()
    private val booksAPI = bookRepositoryFactory.api
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
                    Log.d("tag1", it.toString())
                    it?.let{ b -> totalCount = b.content.total_count }
                    kotlin.runCatching {
                        withContext(scope.coroutineContext) {
                            insertAuthorPublisher(it)
                            insertBook(it)
                        }
                    }.onSuccess {
                        nextPage += 1
                    }.onFailure {
                        errorFeedbackHandler.postValue(ErrorFeedback.DatabaseErrorFeedback)
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
                            Log.d("tag1", nextPage.toString())
                            insertAuthorPublisher(it)
                            insertBook(it)
                        }
                    }.onSuccess {
                        nextPage += 1
                    }.onFailure {
                        errorFeedbackHandler.postValue(ErrorFeedback.DatabaseErrorFeedback)
                    }
                }
        }
    }

    private fun insertAuthorPublisher(response: BookResponse<PaginateBook>?) {
        response?.let {
            it.content.books.forEach { book ->
                book.author?.let { ele ->
                    scope.launch(Dispatchers.IO) {
                        Log.d("tag", "insert author")
                        authorsDB.find(ele.id) ?: {
                            scope.launch(Dispatchers.IO) {
                                val new = AuthorSchema(
                                    ele.id,
                                    ele.name
                                )
                                authorsDB.insert(listOf(new))
                            }
                        }()
                    }
                }
                book.publisher?.let { ele ->
                    scope.launch(Dispatchers.IO) {
                        Log.d("tag", "insert publisher")
                        publishersDB.find(ele.id) ?: {
                            scope.launch(Dispatchers.IO) {
                                val new =
                                    PublisherSchema(
                                            ele.id,
                                        ele.name
                                    )
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
            scope.launch(Dispatchers.IO) {
                Log.d("tag", "insert book!!!!!")
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
                Log.d("tag", b.toString())
                booksDB.insert(b)
            }
        }
    }

//    fun clear() {
//        disposable.clear()
//    }


    private suspend fun callApiAsync(page: Int, perPage:Int, query: BookListQuery) = scope.async {
        kotlin.runCatching {
            loadState.postValue(LoadState.Loading)
            val response = booksAPI.getBooks(page, perPage, query.getStateStr(), query.book)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw BadRequestException(response.errorBody()?.toString())
            }
        }.onFailure {
            Log.d("tag", it.toString())
            if (it is ConnectException){
                errorFeedbackHandler.postValue(ErrorFeedback.ApiNotReachErrorFeedback)
            } else {
                errorFeedbackHandler.postValue(ErrorFeedback.ApiErrorFeedback)
            }
        }.also {
            loadState.postValue(LoadState.Loaded)
        }
    }

    fun changeQuery(query: BookListQuery) {
        this.query = query
    }
}
