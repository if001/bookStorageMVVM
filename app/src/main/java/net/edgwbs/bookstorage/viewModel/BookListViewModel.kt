package net.edgwbs.bookstorage.viewModel

import android.app.Application
import android.app.SharedElementCallback
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.BookRepository
import kotlin.concurrent.thread

class BookListViewModel(application: Application): AndroidViewModel(application) {
    private val repository:BookRepository = BookRepository.instance
    private var bookListLiveData: MutableLiveData<List<Book>> = MutableLiveData()
    private var cachedBookList: MutableList<Book> = mutableListOf()

    private val perPage: Int = 20

    var page: Int = 1
    var totalCount: Int = 0

    init {
        clearCachedBook()
        loadBookList(1, null) {}
    }

    fun clearCachedBook() {
        cachedBookList = mutableListOf()
    }

    fun getLiveData(): MutableLiveData<List<Book>> = bookListLiveData

    fun nextLoadBookList(state: String?, requestEndCallback: () -> Unit) {
        if (perPage * page < totalCount) {
            page += 1
            loadBookList(page, state, requestEndCallback)
        }
    }

    fun loadBookList(page: Int, state: String?, requestEndCallback:() -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                val request = repository.getBooks(page, perPage, state)
                Log.d("uuuuuuuuuuu", request.toString())
                if (request.isSuccessful) {
                    request.body()?.content?.let {
                        totalCount = it.totalCount
                        cachedBookList.addAll(it.books)

                        cachedBookList.forEach{ x -> Log.d("taaaa",x.id.toString() + "------" + x.title)}

                        bookListLiveData.postValue(cachedBookList.toList())
                    }
                } else{
                    request.errorBody()?.let{
                        Log.d("eeeeeeee", it.string())
                    }
                }
            }.onFailure {
                Log.d("eeeeeeee", it.toString())
                it.stackTrace
            }.also {
                Thread.sleep(4000)
                requestEndCallback()
            }
        }
    }
}