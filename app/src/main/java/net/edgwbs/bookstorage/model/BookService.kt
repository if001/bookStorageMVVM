package net.edgwbs.bookstorage.model

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Path

interface BookService {
    @GET("books")
    suspend fun getBooks(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("status") status: String?
    ): Response<BookResponse<PaginateBook>>

    @GET("book/{id}")
    suspend fun findBook(@Path("id") id: Long): Response<BookResponse<Book>>
}
