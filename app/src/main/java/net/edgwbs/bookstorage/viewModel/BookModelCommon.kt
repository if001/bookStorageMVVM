package net.edgwbs.bookstorage.viewModel

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.*
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory
import net.edgwbs.bookstorage.repositories.api.BookRepository
import net.edgwbs.bookstorage.utils.BadRequestException
import net.edgwbs.bookstorage.utils.ErrorFeedback
import net.edgwbs.bookstorage.utils.InternalErrorException

object BookModelCommon {
    fun changeState(book: Book, scope: CoroutineScope,
                    bookRepositoryFactory: BookRepositoryFactory,
                    errorFeedbackHandler: MutableLiveData<ErrorFeedback>) {
        val booksAPI = bookRepositoryFactory.getAPI()
        val booksDB = bookRepositoryFactory.getDB()

        scope.launch(Dispatchers.IO) {
            val result = kotlin.runCatching {
                val response = when (book.readState) {
                    ReadState.NotRead.value -> booksAPI.bookReadStart(book.id)
                    ReadState.Reading.value -> booksAPI.bookReadEnd(book.id)
                    ReadState.Read.value -> booksAPI.bookReadStart(book.id)
                    else -> null
                } ?: throw InternalErrorException()

                if (response.body() !== null){
                    throw InternalErrorException()
                } else {
                    if (!response.isSuccessful) {
                        throw BadRequestException("change state")
                    }
                    if (response.body() == null) {
                        throw InternalErrorException()
                    }
                    response.body()!!
                }
            }

            result.onSuccess {
                    val updatedBook = it.content
                    scope.launch(Dispatchers.IO) {
                        kotlin.runCatching {
                            booksDB.booksDao().update(updatedBook.toSchema())
                        }.onFailure {th ->
                            errorFeedbackHandler.postValue(ErrorFeedback.DatabaseErrorFeedback(th.toString()))
                        }
                    }
            }.onFailure {th ->
                errorFeedbackHandler.postValue(ErrorFeedback.ApiNotReachErrorFeedback(th.toString()))
            }
        }
    }
}
