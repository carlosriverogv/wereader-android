package tfg.carlos.wereaderapp.data.model.library

import com.google.gson.annotations.SerializedName

data class AddBookResponse (
    @SerializedName("message")
    val message: String,
    @SerializedName("ok")
    val ok: Boolean,
)