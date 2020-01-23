package net.edgwbs.bookstorage.model

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.Request
import kotlinx.coroutines.*

 class BookServiceMock: BookService {
     override suspend fun findBook(id: Long): Response<BookResponse<Book>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun createBook(bookForm: BookForm): Response<BookResponse<Book>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun createBookWith(bookForm: BookFormWith): Response<BookResponse<Book>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun getAuthors(): Response<BookResponse<MutableList<CountedAuthor?>>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun createAuthors(authorForm: AuthorForm): Response<BookResponse<Author>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun getPublishers(): Response<BookResponse<MutableList<CountedPublisher?>>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun createPublisher(publisherForm: PublisherForm): Response<BookResponse<Publisher>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun bookStateStart(id: Long): Response<Void> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun bookStateEnd(id: Long): Response<Void> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun getBooks(
         page: Int,
         perPage: Int,
         status: String?
     ): Response<BookResponse<PaginateBook>> {
         return Response.success(BookResponse(PaginateBook.mockBooks(page, perPage)))
     }
}
