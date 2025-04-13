package tfg.carlos.wereaderapp.data.model.auth


import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("authorFav")
    val authorFav: String,
    @SerializedName("avatar")
    val avatar: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("genderFav")
    val genderFav: String,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("tag")
    val tag: String
)