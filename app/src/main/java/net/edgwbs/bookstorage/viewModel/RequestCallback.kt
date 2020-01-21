package net.edgwbs.bookstorage.viewModel

interface RequestCallback {
    fun onRequestSuccess(): Unit
    fun onRequestFail(): Unit
    fun onFail(): Unit
    fun onFinal(): Unit
}
