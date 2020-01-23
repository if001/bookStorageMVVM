package net.edgwbs.bookstorage.model

import android.util.Log
import java.util.*

data class BookResponse<T> (
    val content: T
)

data class PaginateBook (
    val books: List<Book>,
    val total_count: Long // TODO camel case 対応できない？？？
){
    companion object {
        private const val totalCount: Long = 5
        private val t = (0..totalCount).map{ Book.createMock(it.toLong(), "mock title$it", null) }
        fun mockBooks(page:Int, perPage:Int): PaginateBook {
            var start = (page-1)*perPage
            var end = page*perPage
            if (page*perPage > totalCount) {
                start = 0
                end = 0
            }
            if (perPage > totalCount) {
                end = totalCount.toInt()
            }
            if (perPage == -1) {
                end = totalCount.toInt()
            }
            return PaginateBook(t.subList(start, end), totalCount)
        }
    }
}

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
        fun forEmpty(): Book {
            return Book(-2, "", "", null, null, null,null,null,1,null,null,null)
        }

        fun createMock(id: Long, title: String, state: ReadState?): Book {
            val s = state?.value ?: ReadState.NotRead.value
            return Book(id, "", title, null, null, null,null,null, s,null,null,null)
        }
    }
}

enum class ReadState(val value: Int) {
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

data class CountedAuthor(
    val id: Long,
    val name: String,
    val count: Long
)

data class CountedPublisher(
    val id: Long,
    val name: String,
    val count: Long
)

// form
data class BookForm (
    val title: String,
    val isbn: String?,
    val authorId: Long?,
    val publisherId: Long?,
    val smallImageUrl: String?,
    val MediumImageUrl: String?,
    val itemUrl: String?,
    val affiliateUrl: String?
)

data class AuthorForm (
    val authorName: String
)

data class PublisherForm (
    val publisherName: String
)

data class BookFormWith (
    val title: String,
    val isbn: String?,
    val authorName: String?,
    val publisherName: String?,
    val smallImageUrl: String?,
    val MediumImageUrl: String?,
    val itemUrl: String?,
    val affiliateUrl: String?
)

// for rakuten model
data class SearchResult (
    val Items: List<Item>,
    val page: Int,
    val pageCount: Int
)

data class Item (
    val Item: BookResult
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
) {
    fun getInfo(): String {
        val authorName = this.author ?: "not set"
        val publisherName = this.publisherName ?: "not set"
        return "%s (%s)".format(authorName, publisherName)
    }
}

