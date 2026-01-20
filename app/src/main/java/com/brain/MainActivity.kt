package com.brain

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

      override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                        // Simple launcher UI nahi hai abhi
                                startService(Intent(this, WhisperService::class.java))
      }

          fun openTutor() {
                    startService(Intent(this, Tutor::class.java))
          }

              fun openCoach() {
                        startService(Intent(this, Coach::class.java))
              }

                  fun openSearch() {
                            startService(Intent(this, Search::class.java))
                  }

                      fun openMemory() {
                                startService(Intent(this, Memory::class.java))
                      }
}
