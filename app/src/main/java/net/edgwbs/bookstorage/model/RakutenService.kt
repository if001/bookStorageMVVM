package net.edgwbs.bookstorage.model

import retrofit2.http.GET
import retrofit2.http.Query

interface RakutenService {
    @GET("BooksBook/Search/20170404")
    suspend fun getBooks(
        @Query("applicationId") applicationID: String,
        @Query("affiliateId") affiliateID: String,
        @Query("title") title: String?,
        @Query("author") author: String?,
        @Query("page") page: Int,
        @Query("hits") hits: Int
    ): retrofit2.Response<SearchResult>
}
