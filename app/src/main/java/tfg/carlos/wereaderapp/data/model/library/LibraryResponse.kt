package tfg.carlos.wereaderapp.data.model.library


import com.google.gson.annotations.SerializedName
import tfg.carlos.wereaderapp.data.model.book.BookList

data class LibraryResponse(
    @SerializedName("books")
    val books: BookList,
    @SerializedName("dateCreation")
    val dateCreation: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("idUser")
    val idUser: String,
    @SerializedName("__v")
    val v: Int
)