package net.edgwbs.bookstorage.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory
import net.edgwbs.bookstorage.utils.ErrorFeedback

class BookViewModel(application: Application): AndroidViewModel(application) {
    private var bookLiveData: MutableLiveData<Book> = MutableLiveData()
    private val booksRepository: BookRepositoryFactory = BookRepositoryFactory.getInstance(application)

    fun getLiveData(): MutableLiveData<Book> = bookLiveData

    fun loadBook(id: Long,
                 errorFeedbackHandler: MutableLiveData<ErrorFeedback>,
                 loadState: MutableLiveData<LoadState>) {
        viewModelScope.launch {
            Log.d("tag", "load!!!!")
            loadState.postValue(LoadState.Loading)
            kotlin.runCatching {
                withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                    booksRepository.db.booksDao().findByID(id)
                }
            }.onSuccess {
                Log.d("tag", "success!!!")
                if (it != null){
                    Log.d("tag", it.toModel().toString())
                    bookLiveData.postValue(it.toModel())
                } else {
                    errorFeedbackHandler.postValue(ErrorFeedback.DataNotFoundErrorFeedback)
                }
            }.onFailure {
                errorFeedbackHandler.postValue(ErrorFeedback.DataNotFoundErrorFeedback)
            }.also {
                loadState.postValue(LoadState.Loaded)
            }
        }
    }

    fun changeState(book: Book,
                    errorFeedbackHandler: MutableLiveData<ErrorFeedback>,
                    bookStateLoadState: MutableLiveData<LoadState>) {
         BookModelCommon.changeState(book, viewModelScope,
            booksRepository, errorFeedbackHandler, bookStateLoadState)
    }

    fun cancelJob() {
        viewModelScope.cancel()
    }
}

