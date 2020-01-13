package net.edgwbs.bookstorage.model

import retrofit2.http.Query

interface RakutenService {
    suspend fun getBook(
        @Query("applicationId") applicationID: String,
        @Query("affiliateId") affiliateID: String,
        @Query("title") title: String?,
        @Query("author") author: String?,
        @Query("page") page: Int,
        @Query("hits") hits: Int
    ): retrofit2.Response<SearchResult>
}
