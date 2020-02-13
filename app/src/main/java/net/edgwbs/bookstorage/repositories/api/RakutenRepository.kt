package net.edgwbs.bookstorage.repositories.api


import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import net.edgwbs.bookstorage.model.SearchResult
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RakutenRepository{
    private val applicationID: String = "1035362638897131844"
    private val affiliateID = "188fe732.33eb93bf.188fe733.aaced19b"
    private val hits: Int = 30
    private val baseURL = "https://app.rakuten.co.jp/services/api/"

    private val rakutenService: RakutenService = client().create(
        RakutenService::class.java)
    // private val bookService: BookService = BookServiceMock()

    private fun client(): Retrofit {
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() // NamingPoricyを指定する

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseURL)
            .build()
    }

    suspend fun getBooks(page: Int,
                         title: String?,
                         author: String?): Response<SearchResult> {
        return rakutenService.getBooks(applicationID, affiliateID, title, author, page, hits)
    }

    companion object Factory {
        val instance: RakutenRepository
            @Synchronized get() {
                return RakutenRepository()
            }
    }
}