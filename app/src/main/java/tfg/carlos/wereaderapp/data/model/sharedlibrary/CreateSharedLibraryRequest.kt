package tfg.carlos.wereaderapp.data.model.sharedlibrary

import com.google.gson.annotations.SerializedName

data class CreateSharedLibraryRequest (
    @SerializedName("idUserFriend")
    val idUserFriend: String
)