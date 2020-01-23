package net.edgwbs.bookstorage.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.BookRepository

class BookViewModel(application: Application): AndroidViewModel(application) {
    private val repository: BookRepository = BookRepository.instance
    private var bookLiveData: MutableLiveData<Book> = MutableLiveData()


    fun getLiveData(): MutableLiveData<Book> = bookLiveData

    fun loadBook(id: Long, requestCallback: RequestCallback): Job {
        return viewModelScope.launch {
            val result = kotlin.runCatching {
                val response = repository.findBook(id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        bookLiveData.postValue(it.content)
                    }
                    null
                } else {
                    response.errorBody()
                }
            }
            result
                .onSuccess {
                    if (it == null) {
                        requestCallback.onRequestSuccess()
                    } else {
                        requestCallback.onRequestFail()
                    }
                }
                .onFailure {
                    requestCallback.onRequestFail()
                }
                .also {
                    requestCallback.onFinal()
                }
        }
    }

    fun changeState(book: Book, requestCallback: RequestCallback): Job {
        return BookModelCommon.changeState(book, viewModelScope, repository, requestCallback)
    }
}