package com.kaizen.ai.ui.home

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaizen.ai.ui.theme.NeonBlue
import com.kaizen.ai.ui.theme.NeonPurple

/**
 * A stylized 2D animated assistant face for NEZA — not a rigged 3D character (that requires
 * external art assets/motion files), but a lightweight, glanceable "presence" indicator: soft
 * face shape, blinking eyes, a mouth that animates while NEZA is speaking, and a glowing ring
 * while she's listening — similar in spirit to Jarvis/voice-assistant UIs.
 */
@Composable
fun NezaAvatar(state: OrbState, modifier: Modifier = Modifier, size: Dp = 240.dp) {
    val transition = rememberInfiniteTransition(label = "neza")

    // Blink cycle: eyes are open most of the time, closed briefly and periodically.
    val blink by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse),
        label = "blink"
    )

    // Mouth openness animates faster while speaking, stays mostly closed otherwise.
    val mouthSpeed = if (state == OrbState.SPEAKING) 260 else 1400
    val mouthOpen by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (state == OrbState.SPEAKING) 1f else 0.25f,
        animationSpec = infiniteRepeatable(tween(mouthSpeed, easing = LinearEasing), RepeatMode.Reverse),
        label = "mouth"
    )

    // Listening ring pulses; thinking ring rotates faster.
    val ringPulse by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = if (state == OrbState.LISTENING) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "ring"
    )

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2, this.size.height / 2)
        val faceRadius = (this.size.minDimension / 2) * 0.62f

        // Outer state ring (listening / thinking / speaking / idle)
        val ringColor = when (state) {
            OrbState.LISTENING -> NeonBlue
            OrbState.THINKING -> NeonPurple
            OrbState.SPEAKING -> Color(0xFF4DFFB2)
            OrbState.IDLE -> NeonBlue.copy(alpha = 0.4f)
        }
        drawCircle(
            color = ringColor,
            radius = faceRadius * 1.25f * ringPulse,
            center = center,
            style = Stroke(width = 4.dp.toPx())
        )

        // Face
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFE0D1), Color(0xFFF2B49A)),
                center = center,
                radius = faceRadius
            ),
            radius = faceRadius,
            center = center
        )

        // Hair silhouette (simple stylized shape behind/around the face)
        drawCircle(
            color = Color(0xFF2A1B3D),
            radius = faceRadius * 1.08f,
            center = Offset(center.x, center.y - faceRadius * 0.35f),
            alpha = 0.9f
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFE0D1), Color(0xFFF2B49A)),
                center = center,
                radius = faceRadius
            ),
            radius = faceRadius,
            center = center
        )

        // Eyes (close briefly on the blink cycle)
        val eyeOpenness = if (blink > 0.92f) 0.1f else 1f
        val eyeY = center.y - faceRadius * 0.1f
        val eyeSpacing = faceRadius * 0.42f
        val eyeWidth = faceRadius * 0.22f
        val eyeHeight = faceRadius * 0.14f * eyeOpenness

        listOf(-1f, 1f).forEach { side ->
            drawOval(
                color = Color(0xFF241033),
                topLeft = Offset(
                    center.x + side * eyeSpacing - eyeWidth / 2,
                    eyeY - eyeHeight / 2
                ),
                size = androidx.compose.ui.geometry.Size(eyeWidth, eyeHeight.coerceAtLeast(2f))
            )
        }

        // Mouth (animates while speaking)
        val mouthY = center.y + faceRadius * 0.42f
        val mouthWidth = faceRadius * 0.5f
        val mouthHeight = (faceRadius * 0.28f * mouthOpen).coerceAtLeast(3f)
        drawOval(
            color = Color(0xFFB23A5C),
            topLeft = Offset(center.x - mouthWidth / 2, mouthY - mouthHeight / 2),
            size = androidx.compose.ui.geometry.Size(mouthWidth, mouthHeight)
        )
    }
}
