package tfg.carlos.wereaderapp.ui.reader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.services.locateProgression
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Language
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.FragmentEpubReaderBinding
import tfg.carlos.wereaderapp.utils.ReaderPreferencesManager


class EpubReaderFragment : Fragment(), EpubNavigatorFragment.Listener {

    private val viewModel: ReaderViewModel by activityViewModels()
    private var _binding: FragmentEpubReaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigator: EpubNavigatorFragment
    private lateinit var navigatorFactory: EpubNavigatorFactory

    // Se cargan las preferencias del lector guardadas en SharedPreferences
    private val _preferences: EpubPreferences by lazy {
        ReaderPreferencesManager.loadPreferences(requireContext())
    }
    private val preferences get() = _preferences


    @OptIn(ExperimentalReadiumApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se inicializa el navegador con las preferencias por defecto
        navigatorFactory = EpubNavigatorFactory(
            publication = viewModel.publication,
            configuration = EpubNavigatorFactory.Configuration(
                defaults = EpubDefaults(
                    pageMargins = 1.0,
                    fontSize = 1.0,
                    scroll = false,
                    language = Language("es"),
                )
            )
        )

        // Se inicializa el navegador con las últimas preferencias guardadas
        childFragmentManager.fragmentFactory =
            navigatorFactory.createFragmentFactory(
                initialLocator = viewModel.initialLocator,
                listener = this,
                initialPreferences = preferences
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigator.currentLocator
                    .onEach { locator ->

                        // Se obtiene el progreso de lectura
                        val progression = locator.locations.totalProgression ?: 0.0
                        // Se calcula el porcentaje de progreso
                        val progressPercentage = (progression * 100)

                        // Se obtiene el número total de páginas, el número de páginas restantes y la
                        // página actual
                        val totalPages = viewModel.publication.readingOrder.size
                        val pagesRemaining = ((1.0 - progression) * totalPages).toInt()
                        val currentPage = (progression * totalPages).toInt() + 1
                        // Se crea el texto de progreso
                        val progressText = getString(
                            R.string.reader_progress_detail,
                            currentPage,
                            totalPages,
                            pagesRemaining
                        )
                        // Se actualiza el SeekBar y el texto de progreso
                        binding.progressSlider.progress = progressPercentage.toInt()
                        binding.progressText.text = progressText

                        // Si el progreso ha cambiado, lo guardamos
                        viewModel.saveReadingProgression(locator, progressPercentage)
                    }
                    .launchIn(this)
            }
        }

        // Se inicializa el SeekBar
        binding.progressSlider.max = 100
        binding.progressSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // Acción realizada mientras se mueve el SeekBar: Se muestra una vista en tiempo real
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    lifecycleScope.launch {
                        showPreviewForProgress(progress)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            // Acción realizada al soltar el SeekBar: Se cambia la página
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress ?: return
                val target = progress / 100.0

                lifecycleScope.launch {
                    val locator = viewModel.publication.locateProgression(target)
                        ?: return@launch
                    (navigator as? VisualNavigator)?.go(locator)
                }
            }
        })
    }

    // Se muestra una vista previa del progreso de lectura al mover el SeekBar
    private suspend fun showPreviewForProgress(progress: Int) {
        val target = progress / 100.0
        val locator = viewModel.publication.locateProgression(target) ?: return

        val totalPages = viewModel.publication.readingOrder.size
        val currentPage = (target * totalPages).toInt() + 1
        val progression = locator.locations.totalProgression ?: 0.0
        val progressPercentage = (progression * 100)

        // Se actualiza el SeekBar y el texto de progreso
        val progressText = getString(
            R.string.reader_on_change_progress,
            currentPage,
            progressPercentage.toInt()
        )

        binding.progressText.text = progressText
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se infla el layout
        _binding = FragmentEpubReaderBinding.inflate(inflater, container, false)

        val view = binding.root
        val tag = "EpubNavigatorFragment"

        if (savedInstanceState == null) {
            childFragmentManager.commitNow {
                add(R.id.navigator_container, EpubNavigatorFragment::class.java, null, tag)
            }
        }

        // Se obtiene el fragmento del navegador
        navigator = childFragmentManager.findFragmentByTag(tag) as? EpubNavigatorFragment
            ?: throw IllegalStateException("EpubNavigatorFragment not found")

        // Se incializa el funcionamiento de la toolbar
        setupToolbar()

        applyReaderThemeToUI(preferences.theme ?: Theme.SEPIA)

        return view
    }

    // Función para inicializar la MarterialToolbar
    private fun setupToolbar() {
        binding.readerToolbar.inflateMenu(R.menu.read_options_menu)
        binding.readerToolbar.title = viewModel.publication.metadata.title
        binding.readerToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.opt_user_preferences -> {
                    // Se abre el menú de preferencias del lector
                    showUserPreferences()
                    true
                }
                R.id.opt_more_options -> {
                    //TODO: Se abre el menú de opciones adicionales
                    showMoreOptions()
                    true
                }
                else -> false
            }
        }

        binding.readerToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // Función que muestra el menú de preferencias del lector
    private fun showUserPreferences() {
        // Se vuelve a cargar las preferencias para asegurarse de que están actualizadas
        val latestPreferences = ReaderPreferencesManager.loadPreferences(requireContext())

        // Se crea el fragmento de preferencias
        val dialog = ReaderPreferencesFragment(latestPreferences) { newPrefs ->
            // Se actualizan las preferencias en el navegador
            val editor = navigatorFactory.createPreferencesEditor(latestPreferences)
            editor.apply {
                fontSize.set(newPrefs.fontSize)
                scroll.set(newPrefs.scroll)
                theme.set(newPrefs.theme)
            }
            navigator.submitPreferences(editor.preferences)

            // Se guardan las nuevas preferencias en SharedPreferences
            ReaderPreferencesManager.savePreferences(requireContext(), newPrefs)

            // Se aplica el tema del lector a la UI (Toolbar, ProgressBar y Menú)
            applyReaderThemeToUI(newPrefs.theme ?: Theme.SEPIA)

            // Aplicar tema visual completo (barra de estado y fondos)
            (activity as? ReaderActivity)?.applyReaderTheme()
        }
        // Se muestra el fragmento de preferencias
        dialog.show(childFragmentManager, "readerPrefs")
    }

    private fun showMoreOptions() {
        TODO("Not yet implemented")
    }

    //** Se aplica el tema del lector a la UI (Afecta a Toolbar, ProgressBar y Menú)
    // Los fondos se cambian desde la ReaderActivity (applyReaderTheme())*/
    private fun applyReaderThemeToUI(theme: Theme) {
        val bgColorRes = when (theme) {
            Theme.LIGHT -> R.color.light_textColorPrimary
            Theme.DARK -> R.color.dark_textColorPrimary
            Theme.SEPIA -> R.color.light_textColorPrimary
            else -> R.color.reader_theme_sepia
        }

        val color = requireContext().getColor(bgColorRes)

        // Se ajusta el color de título de la toolbar
        binding.readerToolbar.setTitleTextColor(color)

        // Se ajusta el color de los iconos de la toolbar
        binding.readerToolbar.navigationIcon?.setTint(color)

        // Se ajusta el color del texto de la barra de progreso
        binding.progressText.setTextColor(color)

        // Se ajusta del color del progreso faltante de la barra de progreso (el resto de la barra
        // no es necesario cambiarlo)
        binding.progressSlider.progressBackgroundTintList =
            android.content.res.ColorStateList.valueOf(color)

        // Cambiar el color de los iconos del menú acordion a la preferencia de tema
        for (i in 0 until binding.readerToolbar.menu.size()) {
            val item = binding.readerToolbar.menu.getItem(i)
            item.icon?.setTint(color)
        }
    }

    // Cuando el usuario pulsa un enlace externo, abrimos el navegador
    @ExperimentalReadiumApi
    override fun onExternalLinkActivated(url: AbsoluteUrl) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
        startActivity(intent)
    }
}