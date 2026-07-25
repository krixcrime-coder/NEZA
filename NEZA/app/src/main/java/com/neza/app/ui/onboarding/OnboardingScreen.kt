package com.neza.app.ui.onboarding

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.neza.app.assistant.VoiceAssistantService

/**
 * Requests every permission NEZA needs to function, in one go, on first launch.
 * The user can still deny individual permissions — NEZA degrades gracefully
 * (e.g. voice commands to "call X" fall back to opening the dialer instead of dialing directly).
 */
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val context = LocalContext.current

    val permissions = remember {
        buildList {
            add(Manifest.permission.RECORD_AUDIO)
            add(Manifest.permission.READ_CONTACTS)
            add(Manifest.permission.CALL_PHONE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var requested by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Regardless of what was granted/denied, move on — NEZA works with partial permissions.
        startAssistantServiceIfPossible(context)
        onFinished()
    }

    LaunchedEffect(Unit) {
        val alreadyGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (alreadyGranted) {
            startAssistantServiceIfPossible(context)
            onFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to NEZA", style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(
            "NEZA needs a few permissions to listen for \"Hola NEZA\", open apps, and place calls " +
                "on your behalf. You can change these anytime in phone Settings.",
            style = MaterialTheme.typography.bodyMedium
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        Button(
            onClick = {
                requested = true
                launcher.launch(permissions.toTypedArray())
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant permissions")
        }
    }
}

private fun startAssistantServiceIfPossible(context: android.content.Context) {
    val hasMic = ContextCompat.checkSelfPermission(
        context, Manifest.permission.RECORD_AUDIO
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    if (!hasMic) return // can't run the always-listening service without mic access

    val intent = Intent(context, VoiceAssistantService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.startForegroundService(context, intent)
    } else {
        context.startService(intent)
    }
}
