package net.edgwbs.bookstorage.viewModel
import net.edgwbs.bookstorage.model.BookResponse
import net.edgwbs.bookstorage.model.HandelError

interface RequestCallback {
    fun <T>onRequestSuccess(r: T?): Unit //todo
    fun onRequestFail(): Unit
    fun onFail(e: HandelError): Unit
    fun onFinal(): Unit
}
