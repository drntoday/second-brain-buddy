package com.brain

import android.speech.tts.UtteranceProgressListener

class SimpleUtteranceListener(
    private val onDone: () -> Unit
) : UtteranceProgressListener() {

    override fun onStart(utteranceId: String?) {}

    override fun onDone(utteranceId: String?) {
        onDone()
    }

    override fun onError(utteranceId: String?) {
        onDone()
    }
}
