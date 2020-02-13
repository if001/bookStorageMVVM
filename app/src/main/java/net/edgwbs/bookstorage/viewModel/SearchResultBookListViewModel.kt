package net.edgwbs.bookstorage.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.*
import net.edgwbs.bookstorage.repositories.api.BookRepository
import net.edgwbs.bookstorage.repositories.api.RakutenRepository

class SearchResultBookListViewModel(application: Application): AndroidViewModel(application) {
    private val rakutenRepository: RakutenRepository = RakutenRepository.instance
    private val bookRepository: BookRepository = BookRepository.instance
    private var searchResultsLiveData: MutableLiveData<List<BookResult>> = MutableLiveData()
    private var cachedResultList: MutableList<BookResult> = mutableListOf()

    private val perPage: Int = 20
    var page: Int = 1
    var totalCount: Int = 0

    init {
        clearCachedBook()
    }

    fun clearCachedBook() {
        cachedResultList = mutableListOf()
    }

    fun getLiveData(): MutableLiveData<List<BookResult>> = searchResultsLiveData

    fun nextLoadBookList(title: String?, author: String?, requestCallback: RequestCallback) {
        if (perPage * page < totalCount) {
            page += 1
            loadBookList(page, title, author, requestCallback)
        }
    }

    fun loadBookList(page: Int, title: String?,author: String?, requestCallback: RequestCallback) {
        viewModelScope.launch {
            val result = kotlin.runCatching{
                val response = rakutenRepository.getBooks(page, title, author)
                if (response.isSuccessful){
                    response.body()?.Items?.map{ x ->
                        cachedResultList.add(x.Item)
                    }
                    searchResultsLiveData.postValue(cachedResultList)
                    requestCallback.onRequestSuccess(response.body())
                } else {
                    response.errorBody()
                }
            }
            result
                .onFailure {
                    Log.d("exception:", "load rakuten books fail:$it")
                    requestCallback.onFail(HandelError.apiError) // todo
                }.also {
                    requestCallback.onFinal()
                }
        }
    }

    fun registerBooks(books: MutableList<BookResult?>, requestCallback: RequestCallback) {
        viewModelScope.launch {
            val result = kotlin.runCatching {
                val creates = books.mapNotNull{
                    it?.let { bookResult ->
                        val form = BookFormWith(
                            bookResult.title,
                            bookResult.isbn,
                            bookResult.author,
                            bookResult.publisherName,
                            bookResult.smallImageUrl,
                            bookResult.mediumImageUrl,
                            bookResult.itemUrl,
                            bookResult.affiliateUrl
                        )
                        async { bookRepository.createBookWith(form) }
                    }
                }
                val errors = creates.awaitAll().mapNotNull{
                    it.errorBody()
                }
                if (errors.isEmpty()) {
                    listOf()
                } else {
                    errors
                }
            }
            result
                .onSuccess {
                    if(it.isEmpty()){
                        requestCallback.onRequestSuccess(it)
                    } else {
                        requestCallback.onRequestFail()
                    }
                }
                .onFailure {
                    Log.d("exception:", "register books fail:$it")
                    requestCallback.onFail(HandelError.apiError)
                }
                .also {
                    requestCallback.onFinal()
                }
        }
    }
}
