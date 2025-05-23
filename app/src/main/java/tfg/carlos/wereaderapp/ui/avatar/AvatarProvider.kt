package tfg.carlos.wereaderapp.ui.avatar

import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.model.user.Avatar

object AvatarProvider {
    private val avatarList = listOf(
        Avatar(1, R.drawable.avatar1),
        Avatar(2, R.drawable.avatar2),
        Avatar(3, R.drawable.avatar3),
        Avatar(4, R.drawable.avatar4),
        Avatar(5, R.drawable.avatar5),
        Avatar(6, R.drawable.avatar6),
        Avatar(7, R.drawable.avatar7),
        Avatar(8, R.drawable.avatar8),
        Avatar(9, R.drawable.avatar9),
        Avatar(10, R.drawable.avatar10),
        Avatar(11, R.drawable.avatar11),
        Avatar(12, R.drawable.avatar12),
    )

    fun getAvatarById(id: Int): Avatar? {
        return avatarList.find { it.id == id }
    }

    fun getAllAvatars(): List<Avatar> {
        return avatarList
    }
}