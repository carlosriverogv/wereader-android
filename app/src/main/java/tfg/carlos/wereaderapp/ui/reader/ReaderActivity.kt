package tfg.carlos.wereaderapp.ui.reader

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.readium.r2.navigator.preferences.Theme
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.ActivityReaderBinding
import tfg.carlos.wereaderapp.databinding.FragmentEpubReaderBinding
import tfg.carlos.wereaderapp.utils.ReaderPreferencesManager
import java.io.File
import kotlin.coroutines.resumeWithException

class ReaderActivity : AppCompatActivity() {

    private val viewModel: ReaderViewModel by viewModels()
    private lateinit var binding : ActivityReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        applyReaderTheme()

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

    //** Método para aplicar el tema del lector a la Activity (Afecta a la barra de estado y
    // fondos, ya que cambia el color del fragmentContainer completo) */
    fun applyReaderTheme() {
        // Cargar tema desde preferencias
        val preferences = ReaderPreferencesManager.loadPreferences(this)
        val theme = preferences.theme ?: Theme.SEPIA

        // Aplicar colores a la Activity
        val bgColor = when (theme) {
            Theme.LIGHT -> getColor(R.color.reader_theme_light)
            Theme.DARK -> getColor(R.color.reader_theme_dark)
            Theme.SEPIA -> getColor(R.color.reader_theme_sepia)
            else -> getColor(R.color.reader_theme_sepia)
        }

        binding.fragmentContainer.setBackgroundColor(bgColor)

        // (CORRIGE ERROR) Controlar color de iconos de la barra de estado
        // Esto es necesario ya que con el tema claro del sistema y el tema oscuro del lector,
        // los iconos de la barra de estado no se ven
        val insetsController = WindowInsetsControllerCompat(window, binding.root)

        insetsController.isAppearanceLightStatusBars = when (theme) {
            Theme.DARK -> false
            else -> true
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