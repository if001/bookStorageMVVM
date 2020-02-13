package net.edgwbs.bookstorage.viewModel

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.edgwbs.bookstorage.model.*
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory
import net.edgwbs.bookstorage.utils.ApplicationErrorException
import net.edgwbs.bookstorage.utils.BadRequestException
import net.edgwbs.bookstorage.utils.ErrorFeedback

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
                } ?: throw ApplicationErrorException("bad book state")

                if (response.body() !== null){
                    throw ApplicationErrorException("response body null")
                } else {
                    if (!response.isSuccessful) {
                        throw BadRequestException("change state")
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
                            errorFeedbackHandler.postValue(ErrorFeedback.DatabaseErrorFeedback)
                        }
                    }
            }.onFailure {
                errorFeedbackHandler.postValue(ErrorFeedback.ApiNotReachErrorFeedback)
            }
        }
    }
}
