package tfg.carlos.wereaderapp.utils

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.StringRes
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponseItem

object FriendRequestsMenuHandler {
    fun show(
        context: Context,
        anchorView: View,
        onAccept: () -> Unit,
        onReject: () -> Unit
    ) {
        val popupMenu = PopupMenu(context, anchorView)
        popupMenu.menuInflater.inflate(R.menu.friend_request_options_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_accept_friend_request -> {
                    onAccept()
                    true
                }
                R.id.action_reject_friend_request -> {
                    onReject()
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