package net.edgwbs.bookstorage.model

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookRepository{
    private val baseURL = "http://10.0.2.2:8081"

    private val bookService: BookService = client().create(BookService::class.java)
    // private val bookService: BookService = BookServiceMock()

    private fun client(): Retrofit {
        // val httpLogging = HttpLoggingInterceptor()
        // httpLogging.level = HttpLoggingInterceptor.Level.BODY
        // val httpClientBuilder = OkHttpClient.Builder().addInterceptor(httpLogging)
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() // NamingPoricyを指定する

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(baseURL)
            .build()
    }

    suspend fun getBooks(page: Int, perPage:Int, status: String?): Response<BookResponse<PaginateBook>> = bookService.getBooks(page, perPage, status)
    suspend fun findBook(id: Long): Response<BookResponse<Book>> = bookService.findBook(id)

    companion object Factory {
        val instance: BookRepository
        @Synchronized get() {
            return BookRepository()
        }
    }
}