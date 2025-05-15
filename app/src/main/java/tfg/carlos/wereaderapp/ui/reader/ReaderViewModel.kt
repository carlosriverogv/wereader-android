package tfg.carlos.wereaderapp.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.locateProgression
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import java.io.File

class ReaderViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as WeReaderApplication).weReaderDB
    private val localDataSource = LibraryLocalDataSource(db.bookDao())
    private val remoteDadaSource = LibraryRemoteDadaSource()
    val repository = LibraryRepository(remoteDadaSource, localDataSource)

    lateinit var publication: Publication
    var initialLocator: Locator? = null

    var bookId: String = ""

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

        initialLocator = loadReadingProgression()
            ?: publication.locateProgression(0.0)
    }

    fun saveReadingProgression(locator: Locator, progress: Double) {
        val json = locator.toJSON().toString()
        viewModelScope.launch {
            try {
                repository.updateReadingProgression(bookId, json, progress)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Cargar progreso
    private suspend fun loadReadingProgression(): Locator? {
        return try {
            val json = repository.getReadingProgression(bookId)
            Locator.fromJSON(json?.let { JSONObject(it) })
        } catch (e: Exception) {
            null
        }
    }
}