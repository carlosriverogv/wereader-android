package tfg.carlos.wereaderapp.data.model.user


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("authorFav")
    val authorFav: String,
    @SerializedName("avatar")
    val avatar: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("genderFav")
    val genreFav: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tag")
    val tag: String,
    @SerializedName("__v")
    val v: Int
)