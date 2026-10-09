package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.model.BodyZone

/**
 * Draws an anatomical body template with natural skin gradient tones,
 * contour curves, and muscle relief lines for realistic tattoo preview.
 */
@Composable
fun BodyTemplateCanvas(
    zone: BodyZone,
    modifier: Modifier = Modifier,
    skinTone: Color = Color(0xFFE4BC9E),
    skinShadow: Color = Color(0xFFC79A78)
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0 || h <= 0) return@Canvas

            // Neutral soft studio ambient background
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1E2129), Color(0xFF0F1014)),
                    center = Offset(w / 2f, h / 2f),
                    radius = w * 0.9f
                ),
                size = Size(w, h)
            )

            when (zone) {
                BodyZone.FOREARM -> drawForearmAnatomy(w, h, skinTone, skinShadow)
                BodyZone.WRIST -> drawWristAnatomy(w, h, skinTone, skinShadow)
                BodyZone.COLLARBONE -> drawCollarboneAnatomy(w, h, skinTone, skinShadow)
                BodyZone.SHOULDER -> drawShoulderAnatomy(w, h, skinTone, skinShadow)
                BodyZone.RIBS -> drawRibsAnatomy(w, h, skinTone, skinShadow)
                BodyZone.THIGH -> drawThighAnatomy(w, h, skinTone, skinShadow)
                BodyZone.BACK -> drawBackAnatomy(w, h, skinTone, skinShadow)
                else -> drawForearmAnatomy(w, h, skinTone, skinShadow)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawForearmAnatomy(
    w: Float, h: Float,
    skinTone: Color, skinShadow: Color
) {
    val cx = w / 2f
    val armPath = Path().apply {
        moveTo(cx - w * 0.28f, 0f)
        cubicTo(cx - w * 0.26f, h * 0.35f, cx - w * 0.22f, h * 0.7f, cx - w * 0.18f, h)
        lineTo(cx + w * 0.18f, h)
        cubicTo(cx + w * 0.22f, h * 0.7f, cx + w * 0.26f, h * 0.35f, cx + w * 0.28f, 0f)
        close()
    }

    drawPath(
        armPath,
        brush = Brush.horizontalGradient(
            colors = listOf(skinShadow, skinTone, skinTone.copy(alpha = 0.95f), skinShadow),
            startX = cx - w * 0.3f,
            endX = cx + w * 0.3f
        )
    )

    // Muscle flex contour lines (subtle)
    drawLine(
        color = skinShadow.copy(alpha = 0.4f),
        start = Offset(cx - w * 0.05f, h * 0.15f),
        end = Offset(cx - w * 0.08f, h * 0.55f),
        strokeWidth = 3f
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWristAnatomy(
    w: Float, h: Float,
    skinTone: Color, skinShadow: Color
) {
    val cx = w / 2f
    val wristPath = Path().apply {
        moveTo(cx - w * 0.22f, h * 0.2f)
        cubicTo(cx - w * 0.24f, h * 0.5f, cx - w * 0.26f, h * 0.8f, cx - w * 0.28f, h)
        lineTo(cx + w * 0.28f, h)
        cubicTo(cx + w * 0.26f, h * 0.8f, cx + w * 0.24f, h * 0.5f, cx + w * 0.22f, h * 0.2f)
        // Hand transition
        cubicTo(cx + w * 0.28f, h * 0.1f, cx + w * 0.32f, 0f, cx + w * 0.35f, 0f)
        lineTo(cx - w * 0.35f, 0f)
        cubicTo(cx - w * 0.32f, 0f, cx - w * 0.28f, h * 0.1f, cx - w * 0.22f, h * 0.2f)
        close()
    }

    drawPath(
        wristPath,
        brush = Brush.horizontalGradient(
            colors = listOf(skinShadow, skinTone, skinShadow),
            startX = cx - w * 0.3f,
            endX = cx + w * 0.3f
        )
    )

    // Wrist bone highlight (ulna head)
    drawCircle(
        color = skinTone.copy(alpha = 0.5f),
        radius = w * 0.04f,
        center = Offset(cx - w * 0.18f, h * 0.32f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCollarboneAnatomy(
    w: Float, h: Float,
    skinTone: Color, skinShadow: Color
) {
    val cx = w / 2f
    val chestPath = Path().apply {
        moveTo(0f, h * 0.1f)
        cubicTo(w * 0.3f, h * 0.22f, w * 0.7f, h * 0.22f, w, h * 0.1f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }

    drawPath(
        chestPath,
        brush = Brush.verticalGradient(
            colors = listOf(skinShadow, skinTone, skinTone),
            startY = 0f,
            endY = h
        )
    )

    // Clavicle bone lines (elegant curve)
    val clavicle = Path().apply {
        moveTo(w * 0.08f, h * 0.3f)
        cubicTo(w * 0.3f, h * 0.34f, cx - w * 0.05f, h * 0.38f, cx, h * 0.42f)
        cubicTo(cx + w * 0.05f, h * 0.38f, w * 0.7f, h * 0.34f, w * 0.92f, h * 0.3f)
    }
    drawPath(clavicle, skinShadow.copy(alpha = 0.6f), style = Stroke(4f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawShoulderAnatomy(
    w: Float, h: Float,
    skinTone: Color, skinShadow: Color
) {
    val cx = w / 2f
    val deltoidPath = Path().apply {
        moveTo(cx - w * 0.1f, 0f)
        cubicTo(cx + w * 0.35f, h * 0.1f, cx + w * 0.38f, h * 0.35f, cx + w * 0.25f, h * 0.65f)
        cubicTo(cx + w * 0.18f, h * 0.85f, cx + w * 0.15f, h, cx + w * 0.12f, h)
        lineTo(cx - w * 0.3f, h)
        lineTo(cx - w * 0.3f, 0f)
        close()
    }

    drawPath(
        deltoidPath,
        brush = Brush.radialGradient(
            colors = listOf(skinTone, skinTone, skinShadow),
            center = Offset(cx + w * 0.1f, h * 0.25f),
            radius = w * 0.5f
        )
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRibsAnatomy(
    w: Float, h: Float,
    skinTone: Color, skinShadow: Color
) {
    val torso = Path().apply {
        moveTo(w * 0.15f, 0f)
        cubicTo(w * 0.2f, h * 0.35f, w * 0.1f, h * 0.65f, w * 0.18f, h)
        lineTo(w, h)
        lineTo(w, 0f)
        close()
    }

    drawPath(
        torso,
        brush = Brush.horizontalGradient(
            colors = listOf(skinShadow, skinTone, skinTone),
            startX = 0f,
            endX = w
        )
    )

    // Rib cage contours
    for (i in 0..4) {
        val y = h * (0.25f + i * 0.12f)
        drawLine(
            skinShadow.copy(alpha = 0.35f),
            start = Offset(w * 0.22f, y),
            end = Offset(w * 0.75f, y + h * 0.04f),
            strokeWidth = 3f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawThighAnatomy(
    w: Float, h: Float,
    skinTone: Color, skinShadow: Color
) {
    val cx = w / 2f
    val leg = Path().apply {
        moveTo(cx - w * 0.35f, 0f)
        cubicTo(cx - w * 0.38f, h * 0.35f, cx - w * 0.28f, h * 0.75f, cx - w * 0.22f, h)
        lineTo(cx + w * 0.22f, h)
        cubicTo(cx + w * 0.28f, h * 0.75f, cx + w * 0.38f, h * 0.35f, cx + w * 0.35f, 0f)
        close()
    }

    drawPath(
        leg,
        brush = Brush.horizontalGradient(
            colors = listOf(skinShadow, skinTone, skinTone, skinShadow),
            startX = cx - w * 0.4f,
            endX = cx + w * 0.4f
        )
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBackAnatomy(
    w: Float, h: Float,
    skinTone: Color, skinShadow: Color
) {
    val cx = w / 2f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(skinShadow, skinTone, skinShadow),
            startY = 0f,
            endY = h
        ),
        size = Size(w, h)
    )

    // Spine line
    drawLine(
        skinShadow.copy(alpha = 0.45f),
        start = Offset(cx, 0f),
        end = Offset(cx, h),
        strokeWidth = 4f
    )

    // Scapula (shoulder blades)
    val leftBlade = Path().apply {
        moveTo(cx - w * 0.15f, h * 0.2f)
        cubicTo(cx - w * 0.35f, h * 0.25f, cx - w * 0.3f, h * 0.45f, cx - w * 0.12f, h * 0.5f)
    }
    drawPath(leftBlade, skinShadow.copy(alpha = 0.4f), style = Stroke(3f))

    val rightBlade = Path().apply {
        moveTo(cx + w * 0.15f, h * 0.2f)
        cubicTo(cx + w * 0.35f, h * 0.25f, cx + w * 0.3f, h * 0.45f, cx + w * 0.12f, h * 0.5f)
    }
    drawPath(rightBlade, skinShadow.copy(alpha = 0.4f), style = Stroke(3f))
}
