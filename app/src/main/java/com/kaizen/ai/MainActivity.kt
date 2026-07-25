package com.kaizen.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kaizen.ai.ui.navigation.KaizenNavGraph
import com.kaizen.ai.ui.theme.KaizenAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaizenAITheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KaizenNavGraph()
                }
            }
        }
    }
}
