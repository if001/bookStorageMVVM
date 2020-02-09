package net.edgwbs.bookstorage.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.db.AuthorSchema
import net.edgwbs.bookstorage.model.db.BookSchema
import net.edgwbs.bookstorage.model.db.BooksDB
import net.edgwbs.bookstorage.model.db.PublisherSchema
import retrofit2.Response

class BookBoundaryCallback(
    private val scope: CoroutineScope,
    private val booksDB: BooksDB,
    private val networkState: MutableLiveData<NetworkState>,
    private val perPage: Int
): PagedList.BoundaryCallback<Book>() {
    private val repository:BookRepository = BookRepository.instance

    override fun onZeroItemsLoaded() {
        Log.d("tag", "onZeroItemsLoaded")
        // DBから初回取得時にデータが無いとき
        val query = BookListQuery(null, null)
        scope.launch {
            callApiAsync(1, perPage, query)
                .await()
                .onSuccess {
                    Completable.fromAction {
                        insertAuthorPublisher(it)
                        insertBook(it)
                    }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
        }
    }

    override fun onItemAtEndLoaded(bookAtEnd: Book) {
        Log.d("tag", "onItemAtEndLoaded")
        // DBに2ページ以降のデータが無いとき
        // 引数に前回取得したデータの最後のものが渡される
        val query = BookListQuery(null, null)
        scope.launch {
            callApiAsync(2, perPage, query)
                .await()
                .onSuccess {
                    Completable.fromAction {
                        insertAuthorPublisher(it)
                        insertBook(it)
                    }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
        }
    }

    private fun insertAuthorPublisher(response: Response<BookResponse<PaginateBook>>) {
        val authorsDB = booksDB.authorsDao()
        val publishersDB = booksDB.publishersDao()
        kotlin.runCatching {
            booksDB.runInTransaction {
                response.body()?.let {
                    it.content.books.forEach { book ->
                        book.author?.let { ele ->
                            authorsDB.find(ele.id) ?: {
                                val new = AuthorSchema(ele.id, ele.name)
                                authorsDB.insert(listOf(new))
                            }()
                        }
                        book.publisher?.let { ele ->
                            publishersDB.find(ele.id) ?: {
                                val new = PublisherSchema(ele.id, ele.name)
                                publishersDB.insert(listOf(new))
                            }()
                        }
                    }
                }
            }
        }.onFailure {

        }.also {

        }
    }

    private fun insertBook(response: Response<BookResponse<PaginateBook>>) {
        response.body()?.also {
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
            booksDB.booksDao().insert(b)
        }
}

//    fun clear() {
//        disposable.clear()
//    }


    private suspend fun callApiAsync(page: Int, perPage:Int, query: BookListQuery) = scope.async {
        kotlin.runCatching {
            networkState.postValue(NetworkState.RUNNING)
            repository.getBooks(page, perPage, query.getStateStr(), query.book)
        }
            .onSuccess {
                networkState.postValue(NetworkState.SUCCESS)
            }
            .onFailure {
                Log.d("tag", it.toString())
                Log.d("tag", "fail!!!!!!!!")
                networkState.postValue(NetworkState.FAILED)
            }.also {
                Log.d("tag", "also!!!!!!!!!")
                networkState.postValue(NetworkState.NOTWORK)
            }
    }
}