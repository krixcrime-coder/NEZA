package com.neza.app.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.neza.app.ui.theme.NeonBlue
import com.neza.app.ui.theme.NeonPurple

enum class OrbState { IDLE, LISTENING, THINKING, SPEAKING }

/**
 * The signature animated orb on the Home screen. Pulses gently when idle,
 * spins faster and glows brighter for listening/thinking/speaking states.
 */
@Composable
fun AiOrb(state: OrbState, modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 220.dp) {
    val speedMs = when (state) {
        OrbState.IDLE -> 6000
        OrbState.LISTENING -> 2200
        OrbState.THINKING -> 1400
        OrbState.SPEAKING -> 1800
    }
    val transition = rememberInfiniteTransition(label = "orb")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(speedMs, easing = LinearEasing)),
        label = "rotation"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = if (state == OrbState.IDLE) 1.0f else 1.12f,
        animationSpec = infiniteRepeatable(tween(speedMs / 2, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier = modifier.size(size)) {
        val radius = (size.toPx() / 2) * pulse
        val center = Offset(this.size.width / 2, this.size.height / 2)

        rotate(rotation) {
            drawCircle(
                brush = Brush.sweepGradient(listOf(NeonBlue, NeonPurple, NeonBlue)),
                radius = radius,
                center = center
            )
        }
        // Inner glow / core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                center = center,
                radius = radius * 0.9f
            ),
            radius = radius * 0.7f,
            center = center
        )
    }
}
