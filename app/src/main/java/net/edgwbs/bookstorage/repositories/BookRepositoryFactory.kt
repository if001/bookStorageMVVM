package net.edgwbs.bookstorage.repositories

import android.content.Context
import androidx.room.Room
import net.edgwbs.bookstorage.repositories.api.BookRepository
import net.edgwbs.bookstorage.repositories.db.BooksDB



class BookRepositoryFactory(private val api: BookRepository, private val db: BooksDB) {
    fun getDB(): BooksDB {
        return db
    }
    fun getAPI(): BookRepository {
        return api
    }
    companion object {
        private val DBNAME = "books_test"

        fun build(context: Context): BookRepositoryFactory {
            val api = BookRepository.instance
            val db = Room.databaseBuilder(context, BooksDB::class.java, DBNAME).build()
            return BookRepositoryFactory(api, db)
        }
    }
}