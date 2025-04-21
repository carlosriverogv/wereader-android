package tfg.carlos.wereaderapp.data.model.friendship


import com.google.gson.annotations.SerializedName

data class FriendshipRequest(
    @SerializedName("idUser2")
    val idUser2: String
)