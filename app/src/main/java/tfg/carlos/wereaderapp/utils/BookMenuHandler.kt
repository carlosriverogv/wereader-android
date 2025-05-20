package tfg.carlos.wereaderapp.utils

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.StringRes
import tfg.carlos.wereaderapp.R

object BookMenuHandler {

    fun show(
        context: Context,
        anchorView: View,
        isPending: Boolean,
        updateReading: (Boolean) -> Unit,
        updateMarkReadOrUnreadBook: (Boolean) -> Unit,
        updatePending: (Boolean) -> Unit,
        onRead: (() -> Unit)? = null,
        onDetail: (() -> Unit)? = null
    ) {
        val popupMenu = createPopupMenu(context, anchorView)
        setupMenuItems(context, popupMenu, isPending)
        setMenuClickListener(
            context,
            popupMenu,
            isPending,
            updateReading,
            updateMarkReadOrUnreadBook,
            updatePending,
            onRead,
            onDetail
        )
        popupMenu.show()
    }

    private fun createPopupMenu(context: Context, anchorView: View): PopupMenu {
        return PopupMenu(context, anchorView).apply {
            menuInflater.inflate(R.menu.book_options_menu, menu)
        }
    }

    private fun setupMenuItems(context: Context, popupMenu: PopupMenu, isPending: Boolean) {
        val title = if (isPending)
            context.getString(R.string.library_menu_remove_pending)
        else
            context.getString(R.string.library_menu_add_pending)

        popupMenu.menu.findItem(R.id.action_toggle_pending).title = title
    }

    private fun setMenuClickListener(
        context: Context,
        popupMenu: PopupMenu,
        isPending: Boolean,
        updateReading: (Boolean) -> Unit,
        updateMarkReadOrUnreadBook: (Boolean) -> Unit,
        updatePending: (Boolean) -> Unit,
        onRead: (() -> Unit)? = null,
        onDetail: (() -> Unit)?
    ) {
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_read -> {
                    updateReading(true)
                    // Se ejecuta la lectura del libro con FileReader
                    onRead?.invoke()
                    true
                }
                R.id.action_detail -> {
                    onDetail?.invoke()
                    true
                }
                R.id.action_toggle_pending -> {
                    updatePending(!isPending)
                    val msgRes = if (!isPending)
                        R.string.library_menu_add_pending_response
                    else
                        R.string.library_menu_remove_pending_response
                    showToast(context, msgRes)
                    true
                }
                R.id.action_mark_read -> {
                    val isRead = true
                    updateMarkReadOrUnreadBook(isRead)
                    // TODO: Poner progreso de lectura a 100%
                    showToast(context, R.string.library_menu_mark_read_response)
                    true
                }
                R.id.action_mark_unread -> {
                    val isRead = false
                    updateMarkReadOrUnreadBook(isRead)
                    // TODO: Poner progreso de lectura a 100%
                    showToast(context, R.string.library_menu_mark_unread_response)
                    true
                }
                else -> false
            }
        }
    }

    private fun showToast(context: Context, @StringRes msg: Int) {
        Toast.makeText(context, context.getString(msg), Toast.LENGTH_SHORT).show()
    }
}