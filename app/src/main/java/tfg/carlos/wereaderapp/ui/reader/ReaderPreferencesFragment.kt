package tfg.carlos.wereaderapp.ui.reader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.FragmentReaderPreferencesBinding

class ReaderPreferencesFragment(
    initialPreferences: EpubPreferences,
    private val onPreferencesChanged: (EpubPreferences) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentReaderPreferencesBinding? = null
    private val binding get() = _binding!!

    // Estado mutable de preferencias
    private var currentPreferences: EpubPreferences = initialPreferences

    @OptIn(ExperimentalReadiumApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderPreferencesBinding.inflate(inflater, container, false)

        // Inicializa controles con las preferencias actuales
        binding.textSizeSlider.value = (currentPreferences.fontSize ?: 1.0).toFloat()
        binding.scrollSwitch.isChecked = currentPreferences.scroll ?: false

        val checkedTheme = when (currentPreferences.theme) {
            Theme.LIGHT -> R.id.theme_light
            Theme.DARK -> R.id.theme_dark
            Theme.SEPIA, null -> R.id.theme_sepia
            else -> R.id.theme_sepia
        }
        binding.themeGroup.check(checkedTheme)

        // Listeners que modifican el estado actual
        binding.textSizeSlider.addOnChangeListener { _, value, _ ->
            currentPreferences = currentPreferences.copy(fontSize = value.toDouble())
            onPreferencesChanged(currentPreferences)
        }

        binding.themeGroup.setOnCheckedChangeListener { _, checkedId ->
            val newTheme = when (checkedId) {
                R.id.theme_light -> Theme.LIGHT
                R.id.theme_dark -> Theme.DARK
                R.id.theme_sepia -> Theme.SEPIA
                else -> Theme.SEPIA
            }
            currentPreferences = currentPreferences.copy(theme = newTheme)
            onPreferencesChanged(currentPreferences)
        }

        binding.scrollSwitch.setOnCheckedChangeListener { _, isChecked ->
            currentPreferences = currentPreferences.copy(scroll = isChecked)
            onPreferencesChanged(currentPreferences)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}