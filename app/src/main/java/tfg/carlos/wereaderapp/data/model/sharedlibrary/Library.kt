package tfg.carlos.wereaderapp.data.model.sharedlibrary


import com.google.gson.annotations.SerializedName
import tfg.carlos.wereaderapp.data.model.book.BookList

data class Library(
    @SerializedName("books")
    val books: BookList,
    @SerializedName("_id")
    val id: String
)