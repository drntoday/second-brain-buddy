package com.brain

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    lateinit var speakBtn: Button
    lateinit var output: TextView

    lateinit var phi: Phi3
    val search = Search()
    val memory = Memory()
    lateinit var voice: Voice
    val language = Language()
    val tone = Tone() // Added Tone class instance
    val tutor = Tutor()
    val coach = Coach()
 
    lateinit var wake: WakeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        speakBtn = findViewById(R.id.speak)
        output = findViewById(R.id.output)

        voice = Voice(this)

        phi.load(filesDir.absolutePath + "/phi3/phi3-mini.onnx")

        phi = Phi3(filesDir.absolutePath + "/phi35")
        
        // start background whisper service
        startService(Intent(this, WhisperService::class.java))

        // receive wake events
        registerReceiver(object: android.content.BroadcastReceiver(){
            override fun onReceive(c: android.content.Context?, i: Intent?){

                val text = i?.getStringExtra("text") ?: ""
                processSpeech(text)
            }
        }, 
        android.content.IntentFilter("SOLMIE_WAKE"))

        // 🔥 WAKE WORD SYSTEM
        wake = WakeListener(this) { heard ->

            runOnUiThread {
                processSpeech(heard)
            }
        }

        wake.startListening()

        speakBtn.setOnClickListener {
            output.text = "Wake mode active: say SOLMIE"
        }
    }

    fun processSpeech(userText: String) {

        val lang = language.detect(userText)

        // get past context
        val past = memory.recall()

        // include memory in thinking
        var ai = phi.reply(
            "Past context:\n$past\n\nUser: $userText"
        )

        // 🔥 ADDED: Check for "remember" commands
        if(userText.contains("yaad rakh") ||
           userText.contains("remember")) {

            memory.save(userText)
            ai = "Theek hai dost, maine yaad rakh liya 👍"
        }

        // ---- TUTOR MODE ----
        if(tutor.isStudy(userText)){

            ai = tutor.teach(userText)

            memory.save("study: $userText")

            output.text = ai
            voice.speak(ai, lang)
            return
        }


        // ---- CONVERSATION COACH MODE ----
        if(coach.isCoach(userText)){

            ai = coach.guide(userText)

            memory.save("coach: $userText")

            output.text = ai
            voice.speak(ai, lang)
            return
        }
        
        // Use search for latest info
        if (userText.contains("today") ||
            userText.contains("latest") ||
            userText.contains("aaj") ||
            userText.contains("hun")) {

            ai = search.web(userText)
        }

        // --- SAFETY ---
        if(Safe().check(userText)){

            ai = Safe().message()

        } else {

            // streaming + summarize
            var full = ""

            phi.replyStream(userText) {
                full += it + " "
            }

            ai = Whisper().short(full)
        }

        // only speak if earphone connected
        if(Ear(this).isConnected()){

            voice.speak(ai, lang)

        } else {

            output.text = ai +
              "\n(Connect earphone for voice)"
        }
        
        // Apply Hinglish tone to the response
        ai = tone.makeHinglish(ai) // Added tone transformation

        output.text = ai

        voice.speak(ai, lang)

        memory.save("$userText → $ai")
    }
}
