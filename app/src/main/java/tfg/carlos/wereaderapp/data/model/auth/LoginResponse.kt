package tfg.carlos.wereaderapp.data.model.auth


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("token")
    val token: String,
    @SerializedName("idUser")
    val idUser: String,
)