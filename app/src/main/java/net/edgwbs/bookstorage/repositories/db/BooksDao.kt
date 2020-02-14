package net.edgwbs.bookstorage.model.db


import androidx.annotation.Nullable
import androidx.paging.DataSource
import androidx.room.*
import net.edgwbs.bookstorage.repositories.db.AuthorSchema
import net.edgwbs.bookstorage.repositories.db.BookSchema
import net.edgwbs.bookstorage.repositories.db.BookWithInfoSchema
import net.edgwbs.bookstorage.repositories.db.PublisherSchema


@Dao
interface BooksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<BookSchema>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(posts : BookSchema)

    @Transaction
    @Query("SELECT * FROM books LEFT OUTER JOIN authors ON author_id == authors._author_id LEFT OUTER JOIN publishers ON publisher_id == publishers._publisher_id ORDER BY updatedAt DESC")
    fun loadAll() : DataSource.Factory<Int, BookWithInfoSchema>

    @Query("SELECT count(*) FROM books")
    fun count(): Int

    @Transaction
    @Query("SELECT * FROM books LEFT OUTER JOIN authors ON author_id == authors._author_id LEFT OUTER JOIN publishers ON publisher_id == publishers._publisher_id ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    fun loadAllWithPaged(limit: Int, offset: Int): List<BookWithInfoSchema>

    @Transaction
    @Query("SELECT * FROM books books LEFT OUTER JOIN authors ON author_id == authors._author_id LEFT OUTER JOIN publishers ON publisher_id == publishers._publisher_id Where books.readState == :state ORDER BY updatedAt DESC")
    fun findByState(state: Int): DataSource.Factory<Int, BookWithInfoSchema>

    @Transaction
    @Query("SELECT * FROM books books LEFT OUTER JOIN authors ON author_id == authors._author_id LEFT OUTER JOIN publishers ON publisher_id == publishers._publisher_id Where books.id == :id")
    fun findByID(id: Long?) : BookWithInfoSchema?

    @Transaction
    @Query("SELECT * FROM books LEFT OUTER JOIN authors ON author_id == authors._author_id LEFT OUTER JOIN publishers ON publisher_id == publishers._publisher_id Where books.title == :book OR authors._author_name == :book OR publishers._publisher_name == :book ORDER BY books.updatedAt DESC")
    fun findByBookInfo(book: String?): DataSource.Factory<Int, BookWithInfoSchema>

    @Transaction
    @Query("SELECT * FROM books LEFT OUTER JOIN authors ON author_id == authors._author_id LEFT OUTER JOIN publishers ON publisher_id == publishers._publisher_id Where books.readState == :state AND (books.title == :book OR authors._author_name == :book OR publishers._publisher_name == :book) ORDER BY books.updatedAt DESC")
    fun findByBookInfoAndState(book: String?, state: Int?) :  DataSource.Factory<Int, BookWithInfoSchema>

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

    @Query("SELECT * FROM authors Where authors._author_id == :id")
    fun find(id: Long) : AuthorSchema?
}



@Dao
interface PublishersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<PublisherSchema>)

    @Query("SELECT * FROM publishers Where publishers._publisher_id == :id")
    fun find(id: Long) : PublisherSchema?
}


