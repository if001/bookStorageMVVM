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
    val accountId: String,
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
){
    fun getInfo(): String {
        val authorName = this.author?.name ?: "not set"
        val publisherName = this.publisher?.name ?: "not set"
        return "%s (%s)".format(authorName, publisherName)
    }

    companion object {
        fun forMoreLoad(): Book {
            return Book(-1, "", "", null, null, null,null,null,0,null,null,null)
        }
        fun forEmpty(): Book {
            return Book(-2, "", "", null, null, null,null,null,0,null,null,null)
        }
    }
}

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


// for rakuten model
data class SearchResult (
    val Items: List<Contents>,
    val page: Int,
    val pageCount: Int
)

data class Contents (
    val Item: BookResult,
    val page: Int,
    val perPage: Int
)

data class BookResult (
    val title: String,
    val isbn: String?,
    val author: String?,
    val publisherName: String?,
    val smallImageUrl: String?,
    val mediumImageUrl: String?,
    val itemPrice: Int?,
    val itemUrl: String?,
    val affiliateUrl: String?,
    val itemCaption: String?,
    var isChecked: Boolean = false
)

