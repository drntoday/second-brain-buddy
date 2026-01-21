package com.brain

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import java.util.Locale
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class SpeechController(private val ctx: Context) {

    private lateinit var tts: TextToSpeech
    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest

    private val ui = Handler(Looper.getMainLooper())

    private var pendingOnDone: (() -> Unit)? = null
    private var activeUtteranceId: String? = null

    private val isSpeaking = AtomicBoolean(false)

    /* ---------------- INIT ---------------- */

    fun initTts(onReady: () -> Unit) {
        audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        tts = TextToSpeech(ctx) { status ->
            if (status == TextToSpeech.SUCCESS) {
                setLanguage(LanguageDetector.Lang.HINGLISH)
                setTtsListener()
                onReady()
            }
        }
    }

    /* ---------------- LANGUAGE ---------------- */

    fun setLanguage(lang: LanguageDetector.Lang) {
        val result = tts.setLanguage(lang.locale)

        if (
            result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            tts.setLanguage(Locale.US)
        }
    }

    /* ---------------- SPEAK ---------------- */

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        ui.post {
            // Cancel any ongoing speech first
            interruptInternal(callOnDone = false)

            requestAudioFocus()

            val utteranceId = UUID.randomUUID().toString()
            activeUtteranceId = utteranceId
            pendingOnDone = onDone
            isSpeaking.set(true)

            tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    /* ---------------- INTERRUPTION ---------------- */

    fun interrupt() {
        ui.post {
            interruptInternal(callOnDone = true)
        }
    }

    private fun interruptInternal(callOnDone: Boolean) {
        if (!isSpeaking.get()) return

        try {
            tts.stop()
        } catch (_: Exception) {
        }

        abandonAudioFocus()
        isSpeaking.set(false)

        val cb = pendingOnDone
        pendingOnDone = null
        activeUtteranceId = null

        if (callOnDone) {
            cb?.invoke()
        }
    }

    fun isSpeaking(): Boolean = isSpeaking.get()

    /* ---------------- TTS CALLBACK ---------------- */

    private fun setTtsListener() {
        tts.setOnUtteranceProgressListener(
            SimpleUtteranceListener { finishedId ->
                ui.post {
                    // Ignore callbacks from cancelled / old utterances
                    if (finishedId != activeUtteranceId) return@post

                    abandonAudioFocus()
                    isSpeaking.set(false)

                    val cb = pendingOnDone
                    pendingOnDone = null
                    activeUtteranceId = null

                    cb?.invoke()
                }
            }
        )
    }

    /* ---------------- AUDIO ---------------- */

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
        try {
            audioManager.abandonAudioFocusRequest(focusRequest)
        } catch (_: Exception) {
        }
    }

    /* ---------------- SHUTDOWN ---------------- */

    fun shutdown() {
        ui.post {
            interruptInternal(callOnDone = false)
            try {
                tts.shutdown()
            } catch (_: Exception) {
            }
        }
    }
}
