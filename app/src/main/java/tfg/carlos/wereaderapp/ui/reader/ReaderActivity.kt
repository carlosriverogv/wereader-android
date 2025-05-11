package tfg.carlos.wereaderapp.ui.reader

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.toUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.shared.util.Language
import tfg.carlos.wereaderapp.R
import java.io.File
import kotlin.coroutines.resumeWithException

class ReaderActivity : AppCompatActivity() {
    private val viewModel: ReaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        val filePath = intent.getStringExtra("bookPath") ?: return
        if (filePath.isEmpty()) {
            finish() // Sin ruta, salimos
            return
        }
        // Descargamos el libro de Firebase Storage
        lifecycleScope.launch {
            try {
                val localPath = downloadEpubFromFirebase(filePath)

                // Cargamos la publicación y configuramos todo en el ViewModel
                viewModel.loadPublication(localPath)

                // Ahora insertamos el fragmento lector EPUB
                supportFragmentManager.commitNow {
                    replace(R.id.fragment_container, EpubReaderFragment())
                }
            } catch (e: Exception) {
                // Manejar error de descarga
                e.printStackTrace()
                finish()
            }
        }
    }

    private suspend fun downloadEpubFromFirebase(remotePath: String): String {
        val storageRef = FirebaseStorage.getInstance().reference.child(remotePath)
        val localFile = File(getExternalFilesDir(null), remotePath)

        if (localFile.exists()) return localFile.absolutePath

        localFile.parentFile?.mkdirs()

        return suspendCancellableCoroutine { continuation ->
            storageRef.getFile(localFile)
                .addOnSuccessListener {
                    continuation.resume(localFile.absolutePath, null)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
}