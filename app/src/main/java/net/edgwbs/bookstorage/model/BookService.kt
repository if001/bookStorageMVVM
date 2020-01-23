package net.edgwbs.bookstorage.model

import retrofit2.Response
import retrofit2.http.*

interface BookService {
    @GET("books")
    suspend fun getBooks(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("status") status: String?
    ): Response<BookResponse<PaginateBook>>

    @GET("book/{id}")
    suspend fun findBook(@Path("id") id: Long): Response<BookResponse<Book>>

    @POST("/books")
    suspend fun createBook(@Body bookForm: BookForm): Response<BookResponse<Book>>

    @POST("/books_with")
    suspend fun createBookWith(@Body bookForm: BookFormWith): Response<BookResponse<Book>>

    @GET("counted_authors")
    suspend fun getAuthors(): Response<BookResponse<MutableList<CountedAuthor?>>>

    @POST("author")
    suspend fun createAuthors(@Body authorForm: AuthorForm): Response<BookResponse<Author>>


    @GET("counted_publisher")
    suspend fun getPublishers(): Response<BookResponse<MutableList<CountedPublisher?>>>

    @POST("publisher")
    suspend fun createPublisher(@Body publisherForm: PublisherForm): Response<BookResponse<Publisher>>

    @PUT("/book/{id}/state/start")
    suspend fun bookStateStart(@Path("id") id:Long): Response<Void>

    @PUT("/book/{id}/state/end")
    suspend fun bookStateEnd(@Path("id") id:Long): Response<Void>
}
