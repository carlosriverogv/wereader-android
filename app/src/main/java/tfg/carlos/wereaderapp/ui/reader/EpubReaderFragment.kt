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


class EpubReaderFragment : Fragment(), EpubNavigatorFragment.Listener {

    private val viewModel: ReaderViewModel by activityViewModels()
    private var _binding: FragmentEpubReaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigator: EpubNavigatorFragment


    @OptIn(ExperimentalReadiumApi::class)
    private val _preferences = EpubPreferences(
        fontSize = 1.0,
        theme = Theme.SEPIA,
        scroll = false,
        language = Language("es"),
    )
    private val preferences get() = _preferences


    @OptIn(ExperimentalReadiumApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navigatorFactory = EpubNavigatorFactory(
            publication = viewModel.publication,
            configuration = EpubNavigatorFactory.Configuration(
                defaults = EpubDefaults(
                    pageMargins = 1.2,
                    fontSize = 1.0,
                    scroll = false,
                    language = Language("es"),
                )
            )
        )

        childFragmentManager.fragmentFactory =
            navigatorFactory.createFragmentFactory(
                initialLocator = viewModel.initialLocator,
                listener = this,
                initialPreferences = preferences
            )

        /*val editor = navigatorFactory.createPreferencesEditor(preferences)

        editor.apply {
            fontSize.increment()
            scroll.toggle()
        }

        navigator.submitPreferences(editor.preferences)*/
    }

    // Guardamos el progreso de lectura en la base de datos
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var firstEmissionSkipped = false
        var lastSavedHref: String? = null

        /*viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigator.currentLocator
                    .onEach { locator ->

                        if (!firstEmissionSkipped) {
                            firstEmissionSkipped = true
                            return@onEach
                        }

                        if (locator.href.toString() != lastSavedHref) {
                            lastSavedHref = locator.href.toString()
                            viewModel.saveReadingProgression(locator)
                        }
                    }
                    .launchIn(this)
            }
        }*/
        binding.progressSlider.max = 100
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigator.currentLocator
                    .onEach { locator ->

                        // Actualiza el SeekBar y texto
                        val progression = locator.locations.totalProgression ?: 0.0
                        binding.progressSlider.progress = (progression * 100).toInt()

                        val totalPages = viewModel.publication.readingOrder.size
                        val pagesRemaining = ((1.0 - progression) * totalPages).toInt()
                        val currentPage = (progression * totalPages).toInt() + 1
                        val progressText = getString(
                            R.string.reading_progress_detail,
                            currentPage,
                            totalPages,
                            pagesRemaining
                        )
                        binding.progressText.text = progressText

                        val progress = (locator.locations.totalProgression ?: 0.0) * 100
                        // Todo: guardar progreso para el libro

                        // Guarda solo si ha cambiado de página
                        if (locator.href.toString() != lastSavedHref) {
                            lastSavedHref = locator.href.toString()
                            viewModel.saveReadingProgression(locator, progress)
                        }
                    }
                    .launchIn(this)
            }
        }

        binding.progressSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

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

        return view
    }


    // Función para inicializar la MarterialToolbar
    private fun setupToolbar() {
        binding.readerToolbar.inflateMenu(R.menu.read_options_menu)
        binding.readerToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.opt_user_preferences -> {
                    //TODO: Se abre el editor de preferencias
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

    private fun showUserPreferences() {
        TODO("Not yet implemented")
    }

    private fun showMoreOptions() {
        TODO("Not yet implemented")
    }

    // Cuando el usuario pulsa un enlace externo, abrimos el navegador
    @ExperimentalReadiumApi
    override fun onExternalLinkActivated(url: AbsoluteUrl) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
        startActivity(intent)
    }
}