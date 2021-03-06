package net.edgwbs.bookstorage.repositories.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import net.edgwbs.bookstorage.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookRepository{
    private val baseURL = "http://10.0.2.2:8081"

    private val bookService: BookService = client().create(
        BookService::class.java)
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

    suspend fun getBooks(page: Int, perPage:Int, state: String?, book: String?)
            : Response<BookResponse<PaginateBook>> = bookService.getBooks(page, perPage, state, book)
    suspend fun findBook(id: Long): Response<BookResponse<Book>> = bookService.findBook(id)
    suspend fun createBook(bookForm: BookForm): Response<BookResponse<Book>>
            = bookService.createBook(bookForm)

    suspend fun createBookWith(bookForm: BookFormWith): Response<BookResponse<Book>>
            = bookService.createBookWith(bookForm)

    suspend fun getAuthors(): Response<BookResponse<MutableList<CountedAuthor?>>>
            = bookService.getAuthors()
    suspend fun createAuthors(authorForm: AuthorForm): Response<BookResponse<Author>>
            = bookService.createAuthors(authorForm)

    suspend fun getPublishers(): Response<BookResponse<MutableList<CountedPublisher?>>>
            = bookService.getPublishers()
    suspend fun createPublisher(publisherForm: PublisherForm): Response<BookResponse<Publisher>>
            = bookService.createPublisher(publisherForm)

    suspend fun bookReadStart(id: Long): Response<BookResponse<Book>> = bookService.bookStateStart(id)

    suspend fun bookReadEnd(id: Long): Response<BookResponse<Book>> = bookService.bookStateEnd(id)


    companion object Factory {
        val instance: BookRepository
            @Synchronized get() {
                return BookRepository()
            }
    }
}