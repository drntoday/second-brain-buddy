package com.brain

import android.app.Service
import android.content.Intent
import android.os.IBinder

class WhisperService : Service() {

    lateinit var wake: WakeListener

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        wake = WakeListener(this) { text ->

            val i = Intent("SOLMIE_WAKE")
            i.putExtra("text", text)
            sendBroadcast(i)
        }

        wake.startListening()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
