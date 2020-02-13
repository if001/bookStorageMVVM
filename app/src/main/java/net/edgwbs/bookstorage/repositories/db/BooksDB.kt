package net.edgwbs.bookstorage.repositories.db


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [
        BookSchema::class,
        AuthorSchema::class,
        PublisherSchema::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class BooksDB: RoomDatabase() {
    abstract fun booksDao() : BooksDao
    abstract fun authorsDao() : AuthorsDao
    abstract fun publishersDao() : PublishersDao
}
