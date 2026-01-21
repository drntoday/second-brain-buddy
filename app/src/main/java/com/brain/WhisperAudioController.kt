package com.brain

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import java.util.*

class WhisperAudioController(private val ctx: Context) {

    private val ui = Handler(Looper.getMainLooper())

    private lateinit var tts: TextToSpeech
    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest

    private var pendingOnDone: (() -> Unit)? = null

    fun init(onReady: () -> Unit) {
        audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        tts = TextToSpeech(ctx) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("hi", "IN")
                setListener()
                onReady()
            }
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        requestAudioFocus()
        pendingOnDone = onDone

        tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "SOLMIE_UTTERANCE"
        )
    }

    private fun setListener() {
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

    fun destroy() {
        tts.stop()
        tts.shutdown()
    }
}
