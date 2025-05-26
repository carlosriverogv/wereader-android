package tfg.carlos.wereaderapp.data.model.sharedlibrary


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("dateAuthorization")
    val dateAuthorization: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("idLibrary")
    val idLibrary: String,
    @SerializedName("idUserFriend")
    val idUserFriend: String,
    @SerializedName("idUserOwner")
    val idUserOwner: String,
    @SerializedName("__v")
    val v: Int
)