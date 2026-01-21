package com.brain

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechController(private val ctx: Context) {

    private lateinit var tts: TextToSpeech
    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest

    private val ui = Handler(Looper.getMainLooper())
    private var pendingOnDone: (() -> Unit)? = null

    fun initTts(onReady: () -> Unit) {
        audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        tts = TextToSpeech(ctx) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Default safe language
                setLanguage(LanguageDetector.Lang.HINGLISH)
                setTtsListener()
                onReady()
            }
        }
    }

    /**
     * Safely set TTS language with fallback
     */
    fun setLanguage(lang: LanguageDetector.Lang) {
        val result = tts.setLanguage(lang.locale)

        if (result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            // 🔁 Fallback to English
            tts.setLanguage(Locale.US)
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        ui.post {
            requestAudioFocus()
            pendingOnDone = onDone

            tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "SOLMIE_UTTERANCE"
            )
        }
    }

    private fun setTtsListener() {
        tts.setOnUtteranceProgressListener(
            SimpleUtteranceListener {
                ui.post {
                    abandonAudioFocus()
                    pendingOnDone?.invoke()
                    pendingOnDone = null
                }
            }
        )
    }

    private fun requestAudioFocus() {
        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .build()

        audioManager.requestAudioFocus(focusRequest)
    }

    private fun abandonAudioFocus() {
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    fun shutdown() {
        ui.post {
            tts.stop()
            tts.shutdown()
        }
    }
}
