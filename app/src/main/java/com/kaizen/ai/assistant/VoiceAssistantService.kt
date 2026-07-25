package com.kaizen.ai.assistant

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kaizen.ai.MainActivity

/**
 * Always-listening voice assistant service.
 *
 * IMPORTANT (Android platform limitation, not a bug): while this service is running with the mic
 * active in the background, Android REQUIRES a visible, non-dismissible notification — this cannot
 * be hidden. This is an OS-level privacy protection and applies to every app, not just this one.
 * The service also only runs while the phone is powered on (it restarts on boot via BootReceiver,
 * but cannot run while the device is fully off).
 */
class VoiceAssistantService : Service() {

    private var speechRecognizer: SpeechRecognizer? = null
    private val wakeWord = "hola neza"

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel(this)
        startForeground(NOTIFICATION_ID, buildNotification("Listening for \"Hola NEZA\"…"))
        startListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?) = null

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.w(TAG, "Speech recognition not available on this device")
            return
        }
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val heard = matches?.firstOrNull()?.lowercase().orEmpty()
                    handleHeardText(heard)
                    restartListening()
                }

                override fun onError(error: Int) {
                    // Common on-device errors (silence timeout, no match) — just restart the loop.
                    restartListening()
                }

                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
        restartListening()
    }

    private fun restartListening() {
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        speechRecognizer?.startListening(recognizerIntent)
    }

    private fun handleHeardText(heard: String) {
        if (!heard.contains(wakeWord)) return

        updateNotification("Wake word heard — say a command…")
        val commandText = heard.substringAfter(wakeWord).trim()
        if (commandText.isEmpty()) return

        val command = CommandProcessor.parse(commandText)
        val resultMessage = CommandProcessor.execute(this, command)
        if (resultMessage.isNotEmpty()) {
            updateNotification(resultMessage)
        }
    }

    private fun buildNotification(text: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NEZA")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0, Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, buildNotification(text))
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "VoiceAssistantService"
        private const val CHANNEL_ID = "neza_assistant_channel"
        private const val NOTIFICATION_ID = 1001

        fun ensureNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, "NEZA Assistant", NotificationManager.IMPORTANCE_LOW
                )
                val manager = context.getSystemService(NotificationManager::class.java)
                manager?.createNotificationChannel(channel)
            }
        }
    }
}
