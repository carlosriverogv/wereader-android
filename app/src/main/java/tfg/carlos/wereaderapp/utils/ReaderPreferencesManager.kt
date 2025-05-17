package tfg.carlos.wereaderapp.utils

import android.content.Context
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.util.Language

object ReaderPreferencesManager {
    private const val PREF_NAME = "reader_prefs"
    private const val KEY_FONT_SIZE = "font_size"
    private const val KEY_THEME = "theme"
    private const val KEY_SCROLL = "scroll"

    fun savePreferences(context: Context, preferences: EpubPreferences) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putFloat(KEY_FONT_SIZE, preferences.fontSize?.toFloat() ?: 1.0f)
        editor.putString(KEY_THEME, preferences.theme?.name ?: Theme.SEPIA.name)
        editor.putBoolean(KEY_SCROLL, preferences.scroll ?: false)
        editor.apply()
    }

    @OptIn(ExperimentalReadiumApi::class)
    fun loadPreferences(context: Context): EpubPreferences {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val fontSize = prefs.getFloat(KEY_FONT_SIZE, -1f)
        val theme = prefs.getString(KEY_THEME, null)
        val scroll = prefs.contains(KEY_SCROLL)

        // Si no hay ninguna preferencia guardada, devuleve los valores por defecto
        if (fontSize == -1f && theme == null && !scroll) {
            return EpubPreferences(
                fontSize = 1.0,
                theme = Theme.SEPIA,
                scroll = false,
            )
        }

        return EpubPreferences(
            fontSize = if (fontSize == -1f) 1.0 else fontSize.toDouble(),
            theme = theme?.let { Theme.valueOf(it) } ?: Theme.SEPIA,
            scroll = prefs.getBoolean(KEY_SCROLL, false),
            language = Language("es")
        )
    }
}