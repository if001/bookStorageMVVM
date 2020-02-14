package net.edgwbs.bookstorage.repositories.db


import androidx.annotation.Nullable
import androidx.room.*
import net.edgwbs.bookstorage.model.Author
import net.edgwbs.bookstorage.model.Book
import net.edgwbs.bookstorage.model.Publisher
import java.util.*

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = AuthorSchema::class,
            parentColumns = arrayOf("_author_id"),
            childColumns = arrayOf("author_id")
        ),
        ForeignKey(
            entity = PublisherSchema::class,
            parentColumns = arrayOf("_publisher_id"),
            childColumns = arrayOf("publisher_id")
        )
    ],
    indices = [
        Index(value = ["author_id"]),
        Index(value = ["publisher_id"])
    ]
)
data class BookSchema(
    @PrimaryKey
    val id: Long,
    val accountId: String,
    val title: String,
    @ColumnInfo(name = "author_id")
    val authorId: Long?,
    @ColumnInfo(name = "publisher_id")
    val publisherId: Long?,
    val isbn: String?,
    val smallImageUrl: String?,
    val mediumImageUrl: String?,
    val readState: Int,
    val startAt: Date?,
    val endAt: Date?,
    val AffiliateUrl: String?,
    val createdAt: Date,
    val updatedAt: Date
){
    fun toModel(authorSchema: AuthorSchema?, publisherSchema: PublisherSchema?): Book {
        val a = authorSchema?.let {
            Author(it.id, it.name)
        }
        val p = publisherSchema?.let {
            Publisher(it.id, it.name)
        }
        return Book(
            this.id,
            this.accountId,
            this.title,
            a,
            p,
            this.isbn,
            this.smallImageUrl,
            this.mediumImageUrl,
            this.readState,
            this.startAt,
            this.endAt,
            this.AffiliateUrl,
            this.createdAt,
            this.updatedAt
        )
    }
}

class BookWithInfoSchema{
    @Embedded
    lateinit var book: BookSchema

    @Embedded
    @Nullable
    var authorSchema: AuthorSchema? = null

    @Embedded
    @Nullable
    var publisherSchema: PublisherSchema? = null

//    @Relation(parentColumn = "author_id", entityColumn = "id")
//    var authorSchema: AuthorSchema? = null
//    @Relation(parentColumn = "publisher_id", entityColumn = "id")
//    var publisherSchema: PublisherSchema? = null

    fun toModel(): Book {
        return Book(
            this.book.id,
            this.book.accountId,
            this.book.title,
            this.authorSchema?.toModel(),
            this.publisherSchema?.toModel(),
            this.book.isbn,
            this.book.smallImageUrl,
            this.book.mediumImageUrl,
            this.book.readState,
            this.book.startAt,
            this.book.endAt,
            this.book.AffiliateUrl,
            this.book.createdAt,
            this.book.updatedAt
        )
    }
}

@Entity(tableName = "authors")
data class AuthorSchema(
    @PrimaryKey
    @ColumnInfo(name = "_author_id")
    val id: Long,
    @ColumnInfo(name = "_author_name")
    val name: String = "not set"
){
    fun toModel(): Author {
        return Author(this.id,this.name)
    }
}

@Entity(tableName = "publishers")
data class PublisherSchema(
    @PrimaryKey
    @ColumnInfo(name = "_publisher_id")
    val id: Long,
    @ColumnInfo(name = "_publisher_name")
    val name: String = "not set"
){
    fun toModel(): Publisher {
        return Publisher(this.id,this.name)
    }
}

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date?
            = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long?
            = date?.let { it.time }
}