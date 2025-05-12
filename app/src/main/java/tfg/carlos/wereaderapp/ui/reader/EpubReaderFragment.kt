package tfg.carlos.wereaderapp.ui.reader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
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
    val preferences get() = _preferences


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

        /*viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigator.currentLocator
                    .onEach {
                        viewModel.initialLocator = viewModel.loadReadingProgression()
                        viewModel.saveReadingProgression(it)
                    }
                    .launchIn(this)
            }
        }*/

        var firstEmissionSkipped = false
        var lastSavedHref: String? = null

        viewLifecycleOwner.lifecycleScope.launch {
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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpubReaderBinding.inflate(inflater, container, false)
        val view = binding.root
        val tag = "EpubNavigatorFragment"

        if (savedInstanceState == null) {
            childFragmentManager.commitNow {
                add(R.id.navigator_container, EpubNavigatorFragment::class.java, null, tag)
            }
        }

        navigator = childFragmentManager.findFragmentByTag(tag) as EpubNavigatorFragment

        return view
    }

    // Cuando el usuario pulsa un enlace externo, abrimos el navegador
    @ExperimentalReadiumApi
    override fun onExternalLinkActivated(url: AbsoluteUrl) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
        startActivity(intent)
    }
}