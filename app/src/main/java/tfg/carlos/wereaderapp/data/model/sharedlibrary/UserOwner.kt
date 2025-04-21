package tfg.carlos.wereaderapp.data.model.sharedlibrary


import com.google.gson.annotations.SerializedName

data class UserOwner(
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tag")
    val tag: String
)