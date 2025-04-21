package tfg.carlos.wereaderapp.data.model.library


import com.google.gson.annotations.SerializedName

data class NewLibraryRequest(
    @SerializedName("books")
    val idBooks: List<String>,
    @SerializedName("idUser")
    val idUser: String
)