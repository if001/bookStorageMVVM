package net.edgwbs.bookstorage.model

data class Book(
    val id: Long,
    val title: String,
    val author: Author?,
    val publisher: Publisher?
)

data class Author(
    val id: Long,
    val name: String
)

data class Publisher(
    val id: Long,
    val name: String
)