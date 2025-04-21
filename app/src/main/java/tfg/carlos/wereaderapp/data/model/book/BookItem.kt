package tfg.carlos.wereaderapp.data.model.book


import com.google.gson.annotations.SerializedName

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
    @SerializedName("gender")
    val gender: String,
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