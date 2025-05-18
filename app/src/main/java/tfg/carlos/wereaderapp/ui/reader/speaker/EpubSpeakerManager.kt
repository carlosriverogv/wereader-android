package tfg.carlos.wereaderapp.ui.reader.speaker
/*
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.shared.publication.Publication
import java.util.Locale

class EpubSpeakerManager(
    private val context: Context,
    private val publication: Publication,
    private val navigator: VisualNavigator,
    private val scope: CoroutineScope
) {

    private var tts: TextToSpeech? = null
    private var isSpeaking = false

    fun initialize(language: Locale = Locale("es", "ES"), onReady: () -> Unit = {}) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = language
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        if (isSpeaking) {
                            scope.launch {
                                val success = navigator.goForward()
                                if (success) {
                                    readCurrentSection()
                                } else {
                                    stopReading()
                                }
                            }
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })
                onReady()
            }
        }
    }

    fun readCurrentSection() {
        if (tts == null) return
        val locator = navigator.currentLocator.value ?: return

        scope.launch(Dispatchers.IO) {
            val resource = publication.get(locator.href)
            val content = resource?.read().readBytes().decodeToString()

            val plainText = android.text.Html.fromHtml(content, android.text.Html.FROM_HTML_MODE_LEGACY).toString()
            isSpeaking = true

            tts?.speak(plainText, TextToSpeech.QUEUE_FLUSH, null, "epub_reading")
        }
    }

    fun pause() {
        tts?.stop()
        isSpeaking = false
    }

    fun resume() {
        readCurrentSection()
    }

    fun stopReading() {
        tts?.stop()
        isSpeaking = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}*/