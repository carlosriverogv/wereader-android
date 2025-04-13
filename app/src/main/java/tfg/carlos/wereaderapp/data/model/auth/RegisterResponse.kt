package tfg.carlos.wereaderapp.data.model.auth


import com.google.gson.annotations.SerializedName
import tfg.carlos.wereaderapp.data.model.user.User

data class RegisterResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("user")
    val user: User
)