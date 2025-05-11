package tfg.carlos.wereaderapp.ui.reader

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.locateProgression
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.fromEpubHref
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.mediatype.MediaType
import org.readium.r2.shared.util.toUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.io.File
import kotlin.coroutines.resumeWithException

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var publication: Publication
    var initialLocator: Locator? = null
        private set

    lateinit var navigatorFactory: EpubNavigatorFactory
        private set

    private val prefs: SharedPreferences = application
        .getSharedPreferences("reader_prefs", Context.MODE_PRIVATE)

    suspend fun loadPublication(filePath: String) {
        val context = getApplication<Application>()
        val httpClient = DefaultHttpClient()
        val assetRetriever = AssetRetriever(context.contentResolver, httpClient)

        val opener = PublicationOpener(
            DefaultPublicationParser(context, httpClient, assetRetriever, pdfFactory = null)
        )

        val asset = assetRetriever.retrieve(File(filePath).toUrl()).getOrElse {
            throw Exception("Error al recuperar asset")
        }

        publication = opener.open(asset, allowUserInteraction = true).getOrElse {
            throw Exception("Error al abrir publicación")
        }


        // Intentar recuperar el Locator guardado
        initialLocator = loadReadingProgression()
            ?: publication.locateProgression(0.0)


    }

    fun saveReadingProgression(locator: Locator) {
        val json = locator.toJSON().toString()
        prefs.edit().putString("lastLocator", json).apply()
    }

    // Cargar progreso
    private fun loadReadingProgression(): Locator? {
        val json = prefs.getString("lastLocator", null) ?: return null
        return try {
            Locator.fromJSON(JSONObject(json))
        } catch (e: Exception) {
            null
        }
    }
}