package net.edgwbs.bookstorage.model

import java.util.*

data class BookResponse<T> (
    val content: T
)

data class PaginateBook (
    val books: List<Book>,
    val totalCount: Int
)

data class Book(
    val id: Long,
    val accountID: String,
    val title: String,
    val author: Author?,
    val publisher: Publisher?,
    val isbn: String?,
    val smallImageUrl: String?,
    val mediumImageUrl: String?,
    val readState: Int,
    val startAt: Date?,
    val endAt: Date?,
    val AffiliateUrl: String?
)

enum class ReadState(val state: Int) {
    NotRead(1),
    Reading(2),
    Read(3)
}

data class Author(
    val id: Long,
    val name: String = "not set"
)

data class Publisher(
    val id: Long,
    val name: String = "not set"
)

