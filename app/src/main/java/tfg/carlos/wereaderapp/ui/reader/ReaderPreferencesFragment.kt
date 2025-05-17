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
    private val initialPreferences: EpubPreferences,
    private val onPreferencesChanged: (EpubPreferences) -> Unit
) : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentReaderPreferencesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @OptIn(ExperimentalReadiumApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderPreferencesBinding.inflate(inflater, container, false)

        // Font size
        binding.textSizeSlider.value = (initialPreferences.fontSize ?: 1.0).toFloat()
        binding.textSizeSlider.addOnChangeListener { _, value, _ ->
            val newPrefs = initialPreferences.copy(fontSize = value.toDouble())
            onPreferencesChanged(newPrefs)
        }

        // Theme
        binding.themeGroup.setOnCheckedChangeListener { _, checkedId ->
            val newTheme = when (checkedId) {
                R.id.theme_light -> Theme.LIGHT
                R.id.theme_dark -> Theme.DARK
                R.id.theme_sepia -> Theme.SEPIA
                else -> Theme.SEPIA
            }
            val newPrefs = initialPreferences.copy(theme = newTheme)
            onPreferencesChanged(newPrefs)
        }

        // Scroll
        binding.scrollSwitch.isChecked = initialPreferences.scroll ?: false
        binding.scrollSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newPrefs = initialPreferences.copy(scroll = isChecked)
            onPreferencesChanged(newPrefs)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}