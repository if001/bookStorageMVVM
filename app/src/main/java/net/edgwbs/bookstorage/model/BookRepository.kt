package net.edgwbs.bookstorage.model

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookRepository{
    private val baseURL = "http://10.0.2.1"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    // private val bookService: BookService = retrofit.create(BookService::class.java)
    private val bookService: BookService = BookServiceMock()

    suspend fun getBooks(): Call<List<Book>> = bookService.getBooks()

    companion object Factory {
        val instance: BookRepository
        @Synchronized get() {
            return BookRepository()
        }
    }
}