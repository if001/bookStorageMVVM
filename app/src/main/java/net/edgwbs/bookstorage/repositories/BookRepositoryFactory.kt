package net.edgwbs.bookstorage.repositories

import android.content.Context
import android.util.Log
import androidx.room.Room
import net.edgwbs.bookstorage.repositories.api.BookRepository
import net.edgwbs.bookstorage.repositories.db.BooksDB



class BookRepositoryFactory(val api: BookRepository, val db: BooksDB) {
    companion object {
        private val DBNAME = "books_test"
        private var instance: BookRepositoryFactory? = null

        fun getInstance(context: Context) = instance ?: synchronized(this){
            Log.d("tag", "repository factory!!!!!!!!!!1")
            instance ?: BookRepositoryFactory(
                BookRepository.instance,
                Room.databaseBuilder(context, BooksDB::class.java, DBNAME).build()
            ).also{ instance = it}
        }
    }
}