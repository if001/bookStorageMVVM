package net.edgwbs.bookstorage.model

data class BookResponse<T> (
    val content: T
)

data class PaginateBook (
    val books: List<Book>,
    val totalCount: Int
)

data class Book(
    val id: Long,
    val title: String,
    val author: Author?,
    val publisher: Publisher?

)

data class Author(
    val id: Long,
    val name: String = "not set"
)

data class Publisher(
    val id: Long,
    val name: String = "not set"
)

