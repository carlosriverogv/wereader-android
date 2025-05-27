package tfg.carlos.wereaderapp.data.model.friendship

import com.google.gson.annotations.SerializedName

data class FriendshipResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("ok")
    val ok: Boolean
)
