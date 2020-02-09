package net.edgwbs.bookstorage.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import arrow.core.right
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.*

object BookModelCommon {
    fun changeState(book: Book, scope: CoroutineScope, repository: BookRepository, requestCallback: RequestCallback): Job {
        return scope.launch {
            val result = kotlin.runCatching {
                val response = when (book.readState) {
                    ReadState.NotRead.value -> repository.bookReadStart(book.id)
                    ReadState.Reading.value -> repository.bookReadEnd(book.id)
                    ReadState.Read.value -> repository.bookReadStart(book.id)
                    else -> null
                }
                if (response == null) {
                    // throw IllegalArgumentException("bad status")
                    requestCallback.onRequestFail()
                } else if (response.isSuccessful) {
                    requestCallback.onRequestSuccess(response.body()!!.content)
                } else {
                    // todo error handle
                    throw IllegalArgumentException("network error")
                }
            }
            result
                .onFailure {
                    requestCallback.onFail(HandelError.apiError)
                }
                .also {
                    requestCallback.onFinal()
                }
        }
    }
}
