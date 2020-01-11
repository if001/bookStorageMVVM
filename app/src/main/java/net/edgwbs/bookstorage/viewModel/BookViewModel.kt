package net.edgwbs.bookstorage.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.BookRepository

class BookViewModel(application: Application): AndroidViewModel(application) {
    private val repository: BookRepository = BookRepository.instance
    private var bookLiveData: MutableLiveData<Book> = MutableLiveData()


    init {
        // loadBook()
    }

    fun getLiveData(): MutableLiveData<Book> = bookLiveData

    fun loadBook(id: Long) {
        viewModelScope.launch {
            kotlin.runCatching {
                val request = repository.findBook(id)
                if (request.isSuccessful) {
                    request.body()?.let {
                        bookLiveData.postValue(it.content)
                    }
                }
            }.onFailure {
                it.stackTrace
            }
        }
    }
}