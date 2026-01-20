package com.brain

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class Voice(val ctx: Context) {

 fun listen(lang: String = "en-IN",
            cb: (String)->Unit){

   val sr =
     SpeechRecognizer.createSpeechRecognizer(ctx)

   sr.setRecognitionListener(
     object: android.speech.RecognitionListener{

       override fun onResults(r: Bundle?) {

         val list =
          r?.getStringArrayList(
            SpeechRecognizer.RESULTS_RECOGNITION
          )

         if(!list.isNullOrEmpty())
            cb(list[0])
       }

       override fun onError(e: Int) {}
       override fun onReadyForSpeech(p0: Bundle?) {}
       override fun onBeginningOfSpeech() {}
       override fun onRmsChanged(p0: Float) {}
       override fun onBufferReceived(p0: ByteArray?) {}
       override fun onEndOfSpeech() {}
       override fun onPartialResults(p0: Bundle?) {}
       override fun onEvent(p0: Int, p1: Bundle?) {}
     }
   )

   val i = Intent(
     RecognizerIntent.ACTION_RECOGNIZE_SPEECH
   )

   i.putExtra(
     RecognizerIntent.EXTRA_LANGUAGE, lang)

   sr.startListening(i)
 }
}
