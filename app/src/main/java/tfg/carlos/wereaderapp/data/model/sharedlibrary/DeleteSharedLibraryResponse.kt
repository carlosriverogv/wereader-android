package tfg.carlos.wereaderapp.data.model.sharedlibrary


import com.google.gson.annotations.SerializedName

data class DeleteSharedLibraryResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("ok")
    val ok: Boolean
)