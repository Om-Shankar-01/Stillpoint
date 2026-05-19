package com.example.stillpoint.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class TtsState {
    IDLE,
    INITIALIZING,
    ERROR,
    PLAYING,
}

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private val TAG = "TtsManager"
    private var tts: TextToSpeech? = null

    private val _ttsState = MutableStateFlow(TtsState.INITIALIZING)
    val ttsState: StateFlow<TtsState> = _ttsState.asStateFlow()

    init {
        initialize()
    }

    fun initialize() {
        if (_ttsState.value == TtsState.PLAYING) stop()

        _ttsState.value = TtsState.INITIALIZING
        Log.d(TAG, "Initializing Android TextToSpeech Engine... ")
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "Android TextToSpeech Engine initialized successfully.")

            // Set language to default locale
            val result = tts?.setLanguage(java.util.Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language is not supported or missing data.")
                _ttsState.value = TtsState.ERROR
            } else {
                _ttsState.value = TtsState.IDLE
                setupProgressListener()
            }
        } else {
            _ttsState.value = TtsState.ERROR
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _ttsState.value = TtsState.PLAYING
            }

            override fun onDone(utteranceId: String?) {
                // We use a specific utterance ID "END" to know when the final chunk is done
                if (utteranceId == "END") {
                    _ttsState.value = TtsState.IDLE
                }
            }

            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                _ttsState.value = TtsState.IDLE
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _ttsState.value = TtsState.ERROR
            }
        })
    }

    fun speak(text: String) {
        if (_ttsState.value == TtsState.ERROR || _ttsState.value == TtsState.INITIALIZING) return

        stop()

        // Split by sentences (roughly) to avoid cutting words and provide natural pauses
        val sentences = text.split(Regex("(?<=[.!?])\\s+"))
        val chunks = mutableListOf<String>()
        var currentChunk = StringBuilder()

        for (sentence in sentences) {
            if (currentChunk.length + sentence.length > 3500) {
                chunks.add(currentChunk.toString())
                currentChunk = StringBuilder(sentence)
            } else {
                if (currentChunk.isNotEmpty()) currentChunk.append(" ")
                currentChunk.append(sentence)
            }
        }
        if (currentChunk.isNotEmpty()) {
            chunks.add(currentChunk.toString())
        }

        chunks.forEachIndexed { index, chunk ->
            val mode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            val utteranceId = if (index == chunks.lastIndex) "END" else "CHUNK_$index"

            tts?.speak(chunk, mode, null, utteranceId)
        }
    }

    fun stop() {
        tts?.stop()

        if (_ttsState.value == TtsState.PLAYING) {
            _ttsState.value = TtsState.IDLE
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _ttsState.value = TtsState.IDLE
    }

    fun retryInitialization() {
        if (_ttsState.value == TtsState.ERROR) {
            initialize()
        }
    }
}
