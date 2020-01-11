package net.edgwbs.bookstorage.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.BookRepository

class BookListViewModel(application: Application): AndroidViewModel(application) {
    private val repository:BookRepository = BookRepository.instance
    private var bookListLiveData: MutableLiveData<List<Book>> = MutableLiveData()


    init {
        loadBookList()
    }

    fun getLiveData(): MutableLiveData<List<Book>> = bookListLiveData

    private fun loadBookList() {
        viewModelScope.launch {
            kotlin.runCatching {
                val request = repository.getBooks(1, 5, null)
                Log.d("uuuuuuuuuuu", request.toString())
                if (request.isSuccessful) {
                    request.body()?.content?.books?.let {
                        bookListLiveData.postValue(it)
                    }
                }
            }.onFailure {
                Log.d("eeeeeeee", it.toString())
                it.stackTrace

            }
        }
    }
}