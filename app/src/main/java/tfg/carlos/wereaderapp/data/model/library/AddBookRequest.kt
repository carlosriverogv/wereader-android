package tfg.carlos.wereaderapp.data.model.library


import com.google.gson.annotations.SerializedName

data class AddBookRequest(
    @SerializedName("bookId")
    val bookId: String
)