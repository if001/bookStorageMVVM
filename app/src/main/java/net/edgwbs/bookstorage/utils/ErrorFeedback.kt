package net.edgwbs.bookstorage.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class ErrorFeedback {
    abstract fun getMessage(): String

    object DatabaseErrorFeedback : ErrorFeedback() {
        override fun getMessage(): String = "data base error"
    }
    object ApiNotReachErrorFeedback: ErrorFeedback() {
        override fun getMessage(): String = "api not reach"
    }
    object ApiErrorFeedback: ErrorFeedback() {
        override fun getMessage(): String = "api error"
    }
    object ApplicationErrorFeedback: ErrorFeedback() {
        override fun getMessage(): String = "app error"
    }
    object DataNotFoundErrorFeedback: ErrorFeedback() {
        override fun getMessage(): String = "data not found"
    }
}

data class DatabaseNotReachException(val msg: String?): RuntimeException("database not reach: $msg")
data class ApiNotReachException(val msg: String?): RuntimeException("api not reach: $msg")
data class BadRequestException(val msg: String?): RuntimeException("bad request: $msg")
data class BadBookStateException(val msg: String?): RuntimeException("bad state of book: $msg")
data class ApplicationErrorException(val msg: String?): RuntimeException("application error: $msg")
