package net.edgwbs.bookstorage.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.BookRepository
import net.edgwbs.bookstorage.model.ReadState

object BookModelCommon {
    fun changeState(book: Book, scope: CoroutineScope, repository: BookRepository, requestCallback: RequestCallback): Job {
        return scope.launch {
            val result = kotlin.runCatching {
                val request = when (book.readState) {
                    ReadState.NotRead.value -> repository.bookReadStart(book.id)
                    ReadState.Reading.value -> repository.bookReadEnd(book.id)
                    ReadState.Read.value -> repository.bookReadStart(book.id)
                    else -> null
                }

                if (request == null) {
                    throw IllegalArgumentException("bad status")
                } else if (request.isSuccessful) {
                    // success
                } else {
                    request.errorBody()
                }
            }
            result
                .onSuccess {
                    requestCallback.onRequestSuccess()
                }
                .onFailure {
                    requestCallback.onFail()
                }
                .also {
                    requestCallback.onFinal()
                }
        }
    }
}
