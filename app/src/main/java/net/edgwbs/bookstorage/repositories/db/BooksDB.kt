package net.edgwbs.bookstorage.repositories.db


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.edgwbs.bookstorage.model.db.AuthorsDao
import net.edgwbs.bookstorage.model.db.BooksDao
import net.edgwbs.bookstorage.model.db.PublishersDao


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
