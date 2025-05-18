package tfg.carlos.wereaderapp.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.StringRes
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.ui.reader.ReaderActivity

object BookMenuHandler {

    fun show(
        context: Context,
        anchorView: View,
        isPending: Boolean,
        updateReading: (Boolean) -> Unit,
        updateReadingProgress: (Double) -> Unit,
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
            updateReadingProgress,
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
        updateReadingProgress: (Double) -> Unit,
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
                    // TODO: Ir a la vista de detalle del libro
                    showToast(context, R.string.library_menu_detail)
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
                    val progress = 100.0
                    updateReadingProgress(progress)
                    // TODO: Poner progreso de lectura a 100%
                    showToast(context, R.string.library_menu_mark_read_response)
                    true
                }
                R.id.action_mark_unread -> {
                    val progress = 0.0
                    updateReadingProgress(progress)
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