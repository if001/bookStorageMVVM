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

     override suspend fun bookStateStart(id: Long): Response<BookResponse<Publisher>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun bookStateEnd(id: Long): Response<BookResponse<Publisher>> {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
     }

     override suspend fun getBooks(
         page: Int,
         perPage: Int,
         status: String?
     ): Response<BookResponse<PaginateBook>> {
         val b = mutableListOf(
             Book(1, "1","mock title1", Author(1, "mock author1"), null, null,null,null,1,null,null,null),
             Book(2, "2","mock title2", Author(1, "mock author1"), null, null,null,null,1,null,null,null),
             Book(3, "3","mock title3", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(4, "3","mock title4", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(5, "3","mock title5", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(6, "3","mock title6", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(7, "3","mock title7", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(8, "3","mock title8", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(9, "3","mock title9", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(10, "3","mock title10", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(11, "3","mock title11", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(12, "3","mock title12", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(11, "3","mock title13", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(11, "3","mock title14", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(11, "3","mock title15", Author(2, "mock author2"), null, null,null,null,1,null,null,null),
             Book(11, "3","mock title16", Author(2, "mock author2"), null, null,null,null,1,null,null,null)
             )
         return Response.success(BookResponse(PaginateBook(b, 3)))
     }
}
