package com.brain

import android.app.Activity
import android.os.Bundle
import android.widget.*

class MainActivity : Activity() {

 lateinit var phi: Phi3
 lateinit var dl: ModelDownloader

 override fun onCreate(b: Bundle?) {
  super.onCreate(b)
  setContentView(R.layout.activity_main)

  val status = findViewById<TextView>(R.id.status)
  val progress = findViewById<ProgressBar>(R.id.progress)

  dl = ModelDownloader(this)

  Thread {

   if(!dl.isReady()) {

     dl.downloadAll {
       runOnUiThread {
         progress.progress = it
         status.text = "Downloading brain: $it%"
       }
     }
   }

   phi = Phi3(this)

   runOnUiThread {
     status.text = "Solmie Ready – bolo Solmie"
     startService(
       Intent(this, WakeListener::class.java)
     )
   }

  }.start()

  // BUTTONS
  findViewById<Button>(R.id.tutor)
   .setOnClickListener {
     startService(
       Intent(this, WhisperService::class.java)
         .putExtra("mode","tutor")
     )
   }

  findViewById<Button>(R.id.coach)
   .setOnClickListener {
     startService(
       Intent(this, WhisperService::class.java)
         .putExtra("mode","coach")
     )
   }

  findViewById<Button>(R.id.chat)
   .setOnClickListener {
     startService(
       Intent(this, WhisperService::class.java)
     )
   }
 }
}
