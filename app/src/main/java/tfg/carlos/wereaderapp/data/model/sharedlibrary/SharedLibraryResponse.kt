package tfg.carlos.wereaderapp.data.model.sharedlibrary


import com.google.gson.annotations.SerializedName

data class SharedLibraryResponse(
    @SerializedName("dateAuthorization")
    val dateAuthorization: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("idLibrary")
    val library: Library,
    @SerializedName("idUserFriend")
    val userFriend: String,
    @SerializedName("idUserOwner")
    val userOwner: UserOwner,
    @SerializedName("__v")
    val v: Int
)