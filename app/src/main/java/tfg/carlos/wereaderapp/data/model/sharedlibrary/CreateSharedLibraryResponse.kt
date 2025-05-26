package tfg.carlos.wereaderapp.data.model.sharedlibrary


import com.google.gson.annotations.SerializedName

data class CreateSharedLibraryResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("resultado")
    val sharedLibrary: Result
)