package tfg.carlos.wereaderapp.utils

import android.content.Context
import android.util.Log
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.pdf.PdfDocumentFactory
import org.readium.r2.shared.util.toUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.io.File

class BookReaderManager(private val context: Context) {
    // Instantiate the required components.
    private val httpClient = DefaultHttpClient()
    private val assetRetriever = AssetRetriever(
        contentResolver = context.contentResolver,
        httpClient = httpClient
    )
    private val publicationOpener = PublicationOpener(
        publicationParser = DefaultPublicationParser(
            context,
            httpClient = httpClient,
            assetRetriever = assetRetriever,
            pdfFactory = null,
        )
    )

    suspend fun openBook(epubPath: String): Publication? {
        val url = File(epubPath).toUrl()
        val asset = assetRetriever.retrieve(url).getOrElse {
            Log.e("BookReader", "Error al recuperar asset: ${it.message}")
            return null
        }

        val publication = publicationOpener.open(asset, allowUserInteraction = true).getOrElse {
            Log.e("BookReader", "Error al abrir publicación: ${it.message}")
            return null
        }

        return publication
    }

}