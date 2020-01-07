package net.edgwbs.bookstorage.model

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

interface BookService {
    @GET("books")
    suspend fun getBooks(): Call<List<Book>>
}

class BookServiceMock: BookService {
    override suspend fun getBooks(): Call<List<Book>> {
        return object: Call<List<Book>> {
            override fun enqueue(callback: Callback<List<Book>>) {
            }

            override fun isExecuted(): Boolean {
                return false
            }

            override fun clone(): Call<List<Book>> {
                return this
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun cancel() {
            }

            override fun request(): Request {
                return Request.Builder().build()
            }

            override fun execute(): Response<List<Book>> {
                val b = mutableListOf(
                    Book(1, "mock title", null, null),
                    Book(2, "mock title", null, null),
                    Book(3, "mock title", null, null)
                )
                Log.d("tag", "loading")
                Thread.sleep(2000L)
                Log.d("tag", "loaded!!!!!")
                return Response.success(b)
            }
        }
    }
}
