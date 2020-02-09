package net.edgwbs.bookstorage.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class ErrorFeedback {
    abstract fun getMessage(context: Context): String

    data class ApiErrorFeedback(val message: String, val code: Int): ErrorFeedback() {
        override fun getMessage(context: Context): String = message
    }
    data class ApplicationErrorFeedback(@StringRes val resId: Int) : ErrorFeedback() {
        override fun getMessage(context: Context): String = context.getString(resId)
    }
}