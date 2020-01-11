package net.edgwbs.bookstorage.model

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.Request
import kotlinx.coroutines.*

// class BookServiceMock: BookService {
//    override suspend fun getBooks(page: Int, perPage: Int, status: String?): Call<List<Book>> {
//        return object: Call<List<Book>> {
//            override fun enqueue(callback: Callback<List<Book>>) {
//            }
//
//            override fun isExecuted(): Boolean {
//                return false
//            }
//
//            override fun clone(): Call<List<Book>> {
//                return this
//            }
//
//            override fun isCanceled(): Boolean {
//                return false
//            }
//
//            override fun cancel() {
//            }
//
//            override fun request(): Request {
//                return Request.Builder().build()
//            }
//
//            override fun execute(): Response<List<Book>> {
//                val b = mutableListOf(
//                    Book(1, "mock title1", Author(1, "mock author1"), null),
//                    Book(2, "mock title2", Author(1, "mock author2"), Publisher(1, "mock publisher1")),
//                    Book(3, "mock title3", null, Publisher(2, "mock publisher2"))
//                )
//                Thread.sleep(2000L)
//                return Response.success(b)
//            }
//        }
//    }
//
//    override suspend fun findBook(id: Long): Call<Book> {
//        return object: Call<Book> {
//            override fun enqueue(callback: Callback<Book>) {
//            }
//
//            override fun isExecuted(): Boolean {
//                return false
//            }
//
//            override fun clone(): Call<Book> {
//                return this
//            }
//
//            override fun isCanceled(): Boolean {
//                return false
//            }
//
//            override fun cancel() {
//            }
//
//            override fun request(): Request {
//                return Request.Builder().build()
//            }
//
//            override fun execute(): Response<Book> {
//                // val b = Book(3, "mock detail title", Author(1, "mock author"), Publisher(2, "mock publisher2"))
//                val b = Book(3, "mock detail title", Author(1, "mock author"), null)
//                Thread.sleep(2000L)
//                return Response.success(b)
//            }
//        }
//    }
//}
