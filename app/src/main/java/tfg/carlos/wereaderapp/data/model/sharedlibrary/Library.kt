package tfg.carlos.wereaderapp.data.model.sharedlibrary


import com.google.gson.annotations.SerializedName
import tfg.carlos.wereaderapp.data.model.book.Book

data class Library(
    @SerializedName("books")
    val books: Book,
    @SerializedName("_id")
    val id: String
)