package tfg.carlos.wereaderapp.ui.reader.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.FragmentReaderPreferencesBinding

class ReaderPreferencesFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentReaderPreferencesBinding? = null
    private val binding get() = _binding!!

    @OptIn(ExperimentalReadiumApi::class)
    private var currentPreferences: EpubPreferences = EpubPreferences()
    private var onPreferencesChanged: ((EpubPreferences) -> Unit)? = null

    companion object {
        private const val ARG_FONT_SIZE = "font_size"
        private const val ARG_SCROLL = "scroll"
        private const val ARG_THEME = "theme"

        fun newInstance(preferences: EpubPreferences): ReaderPreferencesFragment {
            val fragment = ReaderPreferencesFragment()
            val args = Bundle().apply {
                putDouble(ARG_FONT_SIZE, preferences.fontSize ?: 1.0)
                putBoolean(ARG_SCROLL, preferences.scroll ?: false)
                putString(ARG_THEME, preferences.theme?.name)
            }
            fragment.arguments = args
            return fragment
        }
    }

    fun setOnPreferencesChangedListener(listener: (EpubPreferences) -> Unit) {
        onPreferencesChanged = listener
    }

    @OptIn(ExperimentalReadiumApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderPreferencesBinding.inflate(inflater, container, false)

        loadPreferencesFromArguments()
        initializeUI()
        setupListeners()

        return binding.root
    }

    @OptIn(ExperimentalReadiumApi::class)
    private fun loadPreferencesFromArguments() {
        val fontSize = arguments?.getDouble(ARG_FONT_SIZE, 1.0) ?: 1.0
        val scroll = arguments?.getBoolean(ARG_SCROLL, false) ?: false
        val themeValue = arguments?.getString(ARG_THEME)
        val theme = themeValue?.let { Theme.valueOf(it) } ?: Theme.SEPIA

        currentPreferences = EpubPreferences(
            fontSize = fontSize,
            scroll = scroll,
            theme = theme
        )
    }

    private fun initializeUI() {
        binding.textSizeSlider.value = (currentPreferences.fontSize ?: 1.0).toFloat()
        binding.scrollSwitch.isChecked = currentPreferences.scroll ?: false

        val checkedTheme = when (currentPreferences.theme) {
            Theme.LIGHT -> R.id.theme_light
            Theme.DARK -> R.id.theme_dark
            Theme.SEPIA, null -> R.id.theme_sepia
            else -> R.id.theme_sepia
        }
        binding.themeGroup.check(checkedTheme)
    }

    @OptIn(ExperimentalReadiumApi::class)
    private fun setupListeners() {
        binding.textSizeSlider.addOnChangeListener { _, value, _ ->
            updatePreferences(currentPreferences.copy(fontSize = value.toDouble()))
        }

        binding.scrollSwitch.setOnCheckedChangeListener { _, isChecked ->
            updatePreferences(currentPreferences.copy(scroll = isChecked))
        }

        binding.themeGroup.setOnCheckedChangeListener { _, checkedId ->
            val newTheme = when (checkedId) {
                R.id.theme_light -> Theme.LIGHT
                R.id.theme_dark -> Theme.DARK
                R.id.theme_sepia -> Theme.SEPIA
                else -> Theme.SEPIA
            }
            updatePreferences(currentPreferences.copy(theme = newTheme))
        }
    }

    private fun updatePreferences(updated: EpubPreferences) {
        currentPreferences = updated
        onPreferencesChanged?.invoke(currentPreferences)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}