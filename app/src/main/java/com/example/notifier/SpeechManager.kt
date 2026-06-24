package com.example.notifier

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isInitialized = false
    
    // Thread-safe set to track active/queued utterance IDs
    private val activeUtterances = ConcurrentHashMap.newKeySet<String>()

    init {
        setupProgressListener()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("en", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("SpeechManager", "en_IN Language is not supported or missing data. Falling back to English Locale.")
                tts?.language = Locale.ENGLISH
            }
            tts?.setSpeechRate(1.0f)
            isInitialized = true
        } else {
            Log.e("SpeechManager", "Initialization of TTS failed")
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                // Utterance started speaking
            }

            override fun onDone(utteranceId: String) {
                activeUtterances.remove(utteranceId)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String) {
                activeUtterances.remove(utteranceId)
            }
            
            override fun onError(utteranceId: String, errorCode: Int) {
                activeUtterances.remove(utteranceId)
            }

            override fun onStop(utteranceId: String, interrupted: Boolean) {
                activeUtterances.remove(utteranceId)
            }
        })
    }

    fun speak(text: String) {
        if (!isInitialized) {
            Log.w("SpeechManager", "TTS not initialized yet")
            return
        }

        // Cap TTS queue at 3
        if (activeUtterances.size >= 3) {
            Log.w("SpeechManager", "Queue limit of 3 reached. Discarding message: $text")
            return
        }

        val utteranceId = UUID.randomUUID().toString()
        activeUtterances.add(utteranceId)

        val result = tts?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            activeUtterances.remove(utteranceId)
            Log.e("SpeechManager", "Failed to queue TTS speak request")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        activeUtterances.clear()
    }
}
