package tfg.carlos.wereaderapp.ui.reader

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.readium.r2.navigator.preferences.ReadingProgression
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.util.AbsoluteUrl
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.FragmentEpubReaderBinding


class EpubReaderFragment : Fragment(), EpubNavigatorFragment.Listener {

    private val viewModel: ReaderViewModel by activityViewModels()
    private var _binding: FragmentEpubReaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigator: EpubNavigatorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        val navigatorFactory = EpubNavigatorFactory(
            publication = viewModel.publication,
            configuration = EpubNavigatorFactory.Configuration(
                defaults = EpubDefaults(
                    pageMargins = 1.4,
                    readingProgression = ReadingProgression.RTL,
                )
            )
        )

        childFragmentManager.fragmentFactory =
            navigatorFactory.createFragmentFactory(
                initialLocator = viewModel.initialLocator,
                listener = this,
                initialPreferences = EpubPreferences(
                    fontSize = 1.0,
                    theme = Theme.SEPIA,
                    scroll = true,

                ),
            )
        super.onCreate(savedInstanceState)
    }

    // Guardamos el progreso de lectura en la base de datos
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = Color.parseColor("#faf4e8")

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                navigator.currentLocator
                    .onEach { viewModel.saveReadingProgression(it) }
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