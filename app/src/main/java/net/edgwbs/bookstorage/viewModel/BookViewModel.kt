package net.edgwbs.bookstorage.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.repositories.api.BookRepository
import net.edgwbs.bookstorage.model.HandelError
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory
import net.edgwbs.bookstorage.utils.ErrorFeedback

class BookViewModel(application: Application): AndroidViewModel(application) {
    private val repository: BookRepository = BookRepository.instance
    private var bookLiveData: MutableLiveData<Book> = MutableLiveData()
    private val booksRepository: BookRepositoryFactory = BookRepositoryFactory.build(application)

    fun getLiveData(): MutableLiveData<Book> = bookLiveData

    fun loadBook(id: Long, requestCallback: RequestCallback): Job {
        return viewModelScope.launch {
            val result = kotlin.runCatching {
                val response = repository.findBook(id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        bookLiveData.postValue(it.content)
                    }
                    requestCallback.onRequestSuccess(response.body())
                } else {
                    // response.errorBody()
                    requestCallback.onFail(HandelError.apiError)
                }
            }
            result
                .onFailure {
                    requestCallback.onRequestFail()
                }
                .also {
                    requestCallback.onFinal()
                }
        }
    }

    fun changeState(book: Book, errorFeedbackHandler: MutableLiveData<ErrorFeedback>) {
        return BookModelCommon.changeState(book, viewModelScope, booksRepository, errorFeedbackHandler)
    }
}