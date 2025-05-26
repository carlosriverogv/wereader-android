package tfg.carlos.wereaderapp.utils

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.StringRes
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponseItem

object FriendMenuHandler {
    fun show(
        context: Context,
        anchorView: View,
        friend: UserFriendshipsResponseItem, // ID del amigo actual
        onToggleShare: () -> Unit,
        onDeleteFriend: () -> Unit
    ) {
        val sessionManager = WeReaderApplication.sessionManager
        val isSharing = sessionManager.isSharingLibrary()
        val friendUserId = sessionManager.getSharedUserId()

        val isSharingWithThisFriend = isSharing && (friend.id == friendUserId)

        val popupMenu = PopupMenu(context, anchorView)
        popupMenu.menuInflater.inflate(R.menu.friend_options_menu, popupMenu.menu)

        val shareTitle = if (isSharingWithThisFriend)
            context.getString(R.string.friend_menu_stop_sharing)
        else
            context.getString(R.string.friend_menu_share_library)

        popupMenu.menu.findItem(R.id.action_toggle_share_library).title = shareTitle

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_toggle_share_library -> {
                    onToggleShare()
                    true
                }
                R.id.action_delete_friend -> {
                    onDeleteFriend()
                    showToast(context, R.string.friend_menu_delete_friend_response)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showToast(context: Context, @StringRes msg: Int) {
        Toast.makeText(context, context.getString(msg), Toast.LENGTH_SHORT).show()
    }
}