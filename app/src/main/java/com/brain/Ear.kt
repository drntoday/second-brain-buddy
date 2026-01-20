package com.brain

import android.content.Context
import android.media.AudioManager

class Ear(val context: Context) {

    fun isConnected(): Boolean {

        val audio =
        context.getSystemService(
            Context.AUDIO_SERVICE
        ) as AudioManager

        return audio.isWiredHeadsetOn ||
               audio.isBluetoothA2dpOn
    }
}
