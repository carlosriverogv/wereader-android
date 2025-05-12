package tfg.carlos.wereaderapp.ui.reader

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import tfg.carlos.wereaderapp.R
import java.io.File
import kotlin.coroutines.resumeWithException

class ReaderActivity : AppCompatActivity() {

    private val viewModel: ReaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_reader)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val remotePath = intent.getStringExtra("bookPath") ?: return
        val bookId = intent.getStringExtra("bookId") ?: return
        viewModel.bookId = bookId

        if (remotePath.isEmpty()) {
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val localPath = getOrDownloadEpub(remotePath)

                // Cargar publicación y crear fragmento
                viewModel.loadPublication(localPath)

                supportFragmentManager.commitNow {
                    replace(R.id.fragment_container, EpubReaderFragment())
                }

            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        }
    }

    /** Obtiene la ruta local del EPUB si ya existe, o lo descarga desde Firebase */
    private suspend fun getOrDownloadEpub(remotePath: String): String {
        val localFile = getLocalEpubFile(remotePath)

        if (localFile.exists()) return localFile.absolutePath

        localFile.parentFile?.mkdirs()

        val storageRef = FirebaseStorage.getInstance().reference.child(remotePath)
        return suspendCancellableCoroutine { continuation ->
            storageRef.getFile(localFile)
                .addOnSuccessListener {
                    continuation.resume(localFile.absolutePath) { cause, _, _ -> null?.let { it1 -> it1(cause) } }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    /** Calcula el archivo local a partir del path remoto */
    private fun getLocalEpubFile(remotePath: String): File {
        val fileName = remotePath.substringAfterLast("/") // solo el nombre
        return File(getExternalFilesDir("epubs"), fileName)
    }
}