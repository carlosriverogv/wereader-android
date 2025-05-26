package tfg.carlos.wereaderapp.data.model.sharedlibrary

import com.google.gson.annotations.SerializedName

data class DeleteSharedLibraryRequest (
    @SerializedName("idOtherUser")
    val idOtherUser: String
)