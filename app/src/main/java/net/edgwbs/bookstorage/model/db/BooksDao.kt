package net.edgwbs.bookstorage.model.db


import androidx.paging.DataSource
import androidx.room.*
import net.edgwbs.bookstorage.model.BookListQuery


@Dao
interface BooksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<BookSchema>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(posts : BookSchema)

    @Transaction
    @Query("SELECT * FROM books ORDER BY updatedAt DESC")
    fun loadAll() : DataSource.Factory<Int, BookWithInfoSchema>

    @Query("SELECT count(*) FROM books")
    fun count(): Int

    @Transaction
    @Query("SELECT * FROM books ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    fun loadAllWithPaged(limit: Int, offset: Int): List<BookWithInfoSchema>

    @Transaction
    @Query("SELECT * FROM books Where books.readState == :state")
    fun findByState(state: Int): DataSource.Factory<Int, BookWithInfoSchema>

    @Transaction
    @Query("SELECT * FROM books Where books.readState == :state")
    fun findByState2(state: Int): List<BookWithInfoSchema>

    @Query("SELECT * FROM books Where books.id == :id")
    fun findByID(id: Long?) : BookSchema

    @Transaction
    @Query("SELECT * FROM books LEFT OUTER JOIN authors ON author_id == authors.id LEFT OUTER JOIN publishers ON publisher_id == publishers.id Where books.title == :book OR authors.author_name == :book OR publishers.publisher_name == :book")
    fun findByBookInfo(book: String?): DataSource.Factory<Int, BookWithInfoSchema>

    @Transaction
    @Query("SELECT * FROM books LEFT OUTER JOIN authors ON author_id == authors.id LEFT OUTER JOIN publishers ON publisher_id == publishers.id Where books.title == :book OR authors.author_name == :book OR publishers.publisher_name == :book")
    fun findByBookInfo2(book: String?): List<BookWithInfoSchema>

    @Transaction
    @Query("SELECT * FROM books LEFT OUTER JOIN authors ON author_id == authors.id LEFT OUTER JOIN publishers ON publisher_id == publishers.id Where books.readState == :state AND (books.title == :book OR authors.author_name == :book OR publishers.publisher_name == :book)")
    fun findByBookInfoAndState(book: String?, state: Int?) :  DataSource.Factory<Int, BookWithInfoSchema>

    @Transaction
    @Query("SELECT * FROM books LEFT OUTER JOIN authors ON author_id == authors.id LEFT OUTER JOIN publishers ON publisher_id == publishers.id Where books.readState == :state AND (books.title == :book OR authors.author_name == :book OR publishers.publisher_name == :book)")
    fun findByBookInfoAndState2(book: String?, state: Int?): List<BookWithInfoSchema>

    @Delete
    fun delete(bookSchema: BookSchema)

    @Query("DELETE FROM books")
    fun deleteAll()

    // @Query("SELECT MAX(sortKey) + 1 FROM posts")
    // fun getNextSortKey() : Int
}



@Dao
interface AuthorsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<AuthorSchema>)

    @Query("SELECT * FROM authors Where authors.id == :id")
    fun find(id: Long) : AuthorSchema?
}



@Dao
interface PublishersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<PublisherSchema>)

    @Query("SELECT * FROM publishers Where publishers.id == :id")
    fun find(id: Long) : PublisherSchema?
}


