package tfg.carlos.wereaderapp.data.model.book


import com.google.gson.annotations.SerializedName
import tfg.carlos.wereaderapp.data.entity.BookEntity

data class BookItem(
    @SerializedName("author")
    val author: String,
    @SerializedName("coverUrl")
    val coverUrl: String,
    @SerializedName("dateCreation")
    val dateCreation: String,
    @SerializedName("datePublished")
    val datePublished: String,
    @SerializedName("downloads")
    val downloads: Int,
    @SerializedName("epubUrl")
    val epubUrl: String,
    @SerializedName("genre")
    val genre: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("isbn")
    val isbn: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("shareable")
    val shareable: Boolean,
    @SerializedName("synopsis")
    val synopsis: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("__v")
    val v: Int
)

fun BookItem.toEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        title = this.title,
        author = this.author,
        coverUrl = this.coverUrl,
        epubUrl = this.epubUrl,
        synopsis = this.synopsis,
        datePublished = this.datePublished,
        price = this.price,
        downloads = this.downloads,
        isbn = this.isbn,
        shareable = this.shareable,
        genre = this.genre,
        dateCreation = this.dateCreation,
        v = this.v
    )
}