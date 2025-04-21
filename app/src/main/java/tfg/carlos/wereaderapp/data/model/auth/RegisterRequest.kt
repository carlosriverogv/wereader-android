package tfg.carlos.wereaderapp.data.model.auth


import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("authorFav")
    var authorFav: String,
    @SerializedName("avatar")
    var avatar: Int,
    @SerializedName("email")
    var email: String,
    @SerializedName("genreFav")
    var genreFav: String,
    @SerializedName("lastname")
    var lastname: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("tag")
    var tag: String
)