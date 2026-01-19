package com.example.goooapp.screenui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme

@Composable
fun InteractiveAuroraBackground(
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val defaultRadiusPx = with(density) { 220.dp.toPx() }

    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    var touchPoint by remember { mutableStateOf<Offset?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing)
        ),
        label = "t"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> touchPoint = offset },
                    onDrag = { change, _ ->
                        touchPoint = change.position
                    },
                    onDragEnd = { touchPoint = null },
                    onDragCancel = { touchPoint = null }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        surfaceVariantColor.copy(alpha = 0.85f)
                    )
                )
            )

            val p = touchPoint ?: center
            val radius = defaultRadiusPx

            val c1 = Color(0xFF6D5DFE)
            val c2 = Color(0xFF00D4FF)
            val c3 = Color(0xFFFF4D8D)

            val o1 = Offset(
                x = p.x + size.width * (0.10f * kotlin.math.sin(t * 2f * Math.PI)).toFloat(),
                y = p.y + size.height * (0.08f * kotlin.math.cos(t * 2f * Math.PI)).toFloat()
            )
            val o2 = Offset(
                x = p.x - size.width * (0.18f * kotlin.math.cos(t * 2f * Math.PI)).toFloat(),
                y = p.y + size.height * (0.14f * kotlin.math.sin(t * 2f * Math.PI)).toFloat()
            )
            val o3 = Offset(
                x = p.x + size.width * (0.22f * kotlin.math.sin((t + 0.25f) * 2f * Math.PI)).toFloat(),
                y = p.y - size.height * (0.16f * kotlin.math.cos((t + 0.25f) * 2f * Math.PI)).toFloat()
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(c1.copy(alpha = 0.80f), Color.Transparent),
                    center = o1,
                    radius = radius * 1.15f
                ),
                radius = radius * 1.15f,
                center = o1
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(c2.copy(alpha = 0.70f), Color.Transparent),
                    center = o2,
                    radius = radius * 1.25f
                ),
                radius = radius * 1.25f,
                center = o2
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(c3.copy(alpha = 0.60f), Color.Transparent),
                    center = o3,
                    radius = radius * 1.35f
                ),
                radius = radius * 1.35f,
                center = o3
            )
        }

        content()
    }
}
