package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders authentic, crisp vector tattoo artwork mathematically.
 * Supports transparency, stroke styling, and real ink blend modes.
 */
@Composable
fun TattooArtRenderer(
    patternType: String,
    modifier: Modifier = Modifier,
    inkColor: Color = Color(0xFF141416),
    accentColor: Color = Color(0xFFE5B54F),
    blendMode: BlendMode = BlendMode.SrcOver,
    alpha: Float = 1.0f
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0 || h <= 0) return@Canvas

            when (patternType) {
                "CYBER_DRAGON" -> drawCyberDragon(w, h, inkColor, accentColor, blendMode, alpha)
                "SNAKE_FLOWER" -> drawSnakeFlower(w, h, inkColor, accentColor, blendMode, alpha)
                "KOI_FISH" -> drawKoiFish(w, h, inkColor, accentColor, blendMode, alpha)
                "MANDALA_COMPASS" -> drawMandalaCompass(w, h, inkColor, accentColor, blendMode, alpha)
                "PANTHER" -> drawPantherHead(w, h, inkColor, accentColor, blendMode, alpha)
                "WHALE_STARS" -> drawWhaleStars(w, h, inkColor, accentColor, blendMode, alpha)
                "KITSUNE_FOX" -> drawKitsuneFox(w, h, inkColor, accentColor, blendMode, alpha)
                "BOTANICAL_SAKURA" -> drawBotanicalSakura(w, h, inkColor, accentColor, blendMode, alpha)
                "RAVEN_RUNES" -> drawRavenRunes(w, h, inkColor, accentColor, blendMode, alpha)
                "BUTTERFLY_GEO" -> drawButterflyGeo(w, h, inkColor, accentColor, blendMode, alpha)
                "TRASH_SAMURAI" -> drawTrashSamurai(w, h, inkColor, Color(0xFFEF4444), blendMode, alpha)
                "TAROT_SUN" -> drawTarotSun(w, h, inkColor, accentColor, blendMode, alpha)
                else -> drawAiCustomDesign(w, h, inkColor, accentColor, blendMode, alpha)
            }
        }
    }
}

private fun DrawScope.drawCyberDragon(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f
    val p = Path()

    // Sharp organic cyber spine
    p.moveTo(cx, cy - h * 0.42f)
    p.cubicTo(cx + w * 0.35f, cy - h * 0.25f, cx - w * 0.38f, cy, cx, cy + h * 0.38f)
    drawPath(p, inkColor, alpha = alpha, style = Stroke(width = w * 0.035f, cap = StrokeCap.Round, join = StrokeJoin.Round), blendMode = blendMode)

    // Cyber barbs & thorns (Left and Right spikes)
    val barbs = Path()
    for (i in -4..4) {
        val yOffset = cy + i * (h * 0.08f)
        val dir = if (i % 2 == 0) 1f else -1f
        barbs.moveTo(cx, yOffset)
        barbs.cubicTo(
            cx + dir * (w * 0.18f), yOffset - h * 0.05f,
            cx + dir * (w * 0.38f), yOffset + h * 0.02f,
            cx + dir * (w * 0.42f), yOffset - h * 0.03f
        )
        barbs.lineTo(cx + dir * (w * 0.22f), yOffset + h * 0.04f)
        barbs.close()
    }
    drawPath(barbs, inkColor, alpha = alpha, blendMode = blendMode)

    // Center jewel / core
    drawCircle(accent, radius = w * 0.04f, center = Offset(cx, cy), alpha = alpha, blendMode = blendMode)
    drawCircle(inkColor, radius = w * 0.06f, center = Offset(cx, cy), alpha = alpha, style = Stroke(w * 0.015f), blendMode = blendMode)
}

private fun DrawScope.drawSnakeFlower(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Moon crescent
    val moon = Path().apply {
        moveTo(cx - w * 0.1f, cy - h * 0.35f)
        cubicTo(cx + w * 0.35f, cy - h * 0.2f, cx + w * 0.35f, cy + h * 0.2f, cx - w * 0.1f, cy + h * 0.35f)
        cubicTo(cx + w * 0.18f, cy + h * 0.15f, cx + w * 0.18f, cy - h * 0.15f, cx - w * 0.1f, cy - h * 0.35f)
        close()
    }
    drawPath(moon, accent, alpha = alpha * 0.85f, blendMode = blendMode)

    // Coiling snake body (S curve)
    val snake = Path().apply {
        moveTo(cx - w * 0.25f, cy - h * 0.38f)
        cubicTo(cx + w * 0.3f, cy - h * 0.25f, cx - w * 0.35f, cy - h * 0.05f, cx + w * 0.2f, cy + h * 0.15f)
        cubicTo(cx + w * 0.38f, cy + h * 0.25f, cx - w * 0.1f, cy + h * 0.38f, cx, cy + h * 0.42f)
    }
    drawPath(snake, inkColor, alpha = alpha, style = Stroke(width = w * 0.04f, cap = StrokeCap.Round), blendMode = blendMode)

    // Snake head
    val head = Path().apply {
        moveTo(cx - w * 0.25f, cy - h * 0.38f)
        lineTo(cx - w * 0.35f, cy - h * 0.42f)
        lineTo(cx - w * 0.28f, cy - h * 0.46f)
        close()
    }
    drawPath(head, inkColor, alpha = alpha, blendMode = blendMode)

    // Peony flower petals in center
    for (angle in 0 until 360 step 60) {
        val rad = Math.toRadians(angle.toDouble()).toFloat()
        val px = cx + cos(rad) * (w * 0.14f)
        val py = cy + sin(rad) * (h * 0.14f)
        drawCircle(inkColor, radius = w * 0.06f, center = Offset(px, py), alpha = alpha, style = Stroke(w * 0.015f), blendMode = blendMode)
    }
}

private fun DrawScope.drawKoiFish(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Water swirl ripples
    drawCircle(accent, radius = w * 0.42f, center = Offset(cx, cy), alpha = alpha * 0.3f, style = Stroke(w * 0.02f), blendMode = blendMode)

    // Koi body arc
    val body = Path().apply {
        moveTo(cx, cy - h * 0.38f)
        cubicTo(cx + w * 0.28f, cy - h * 0.2f, cx + w * 0.25f, cy + h * 0.15f, cx - w * 0.05f, cy + h * 0.32f)
        cubicTo(cx - w * 0.22f, cy + h * 0.15f, cx - w * 0.2f, cy - h * 0.2f, cx, cy - h * 0.38f)
        close()
    }
    drawPath(body, inkColor, alpha = alpha, blendMode = blendMode)

    // Fins and Tail
    val tail = Path().apply {
        moveTo(cx - w * 0.05f, cy + h * 0.32f)
        cubicTo(cx + w * 0.2f, cy + h * 0.42f, cx - w * 0.15f, cy + h * 0.46f, cx - w * 0.22f, cy + h * 0.36f)
    }
    drawPath(tail, inkColor, alpha = alpha, style = Stroke(w * 0.025f, cap = StrokeCap.Round), blendMode = blendMode)
    drawPath(tail, accent, alpha = alpha * 0.8f, style = Stroke(w * 0.015f), blendMode = blendMode)
}

private fun DrawScope.drawMandalaCompass(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Concentric circles
    drawCircle(inkColor, radius = w * 0.42f, center = Offset(cx, cy), alpha = alpha, style = Stroke(w * 0.015f), blendMode = blendMode)
    drawCircle(accent, radius = w * 0.32f, center = Offset(cx, cy), alpha = alpha, style = Stroke(w * 0.012f), blendMode = blendMode)
    drawCircle(inkColor, radius = w * 0.18f, center = Offset(cx, cy), alpha = alpha, style = Stroke(w * 0.02f), blendMode = blendMode)

    // 8-point compass star
    val star = Path()
    for (i in 0 until 8) {
        val outerAngle = Math.toRadians((i * 45).toDouble()).toFloat()
        val innerAngle = Math.toRadians((i * 45 + 22.5).toDouble()).toFloat()
        val outerR = if (i % 2 == 0) w * 0.38f else w * 0.26f
        val innerR = w * 0.12f

        val x1 = cx + cos(outerAngle) * outerR
        val y1 = cy + sin(outerAngle) * outerR
        val x2 = cx + cos(innerAngle) * innerR
        val y2 = cy + sin(innerAngle) * innerR

        if (i == 0) star.moveTo(x1, y1) else star.lineTo(x1, y1)
        star.lineTo(x2, y2)
    }
    star.close()
    drawPath(star, inkColor, alpha = alpha, blendMode = blendMode)

    // Center focal dot
    drawCircle(accent, radius = w * 0.035f, center = Offset(cx, cy), alpha = alpha, blendMode = blendMode)
}

private fun DrawScope.drawPantherHead(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Bold head contour
    val head = Path().apply {
        moveTo(cx - w * 0.32f, cy - h * 0.2f)
        lineTo(cx - w * 0.4f, cy - h * 0.35f) // ear left
        lineTo(cx - w * 0.18f, cy - h * 0.3f)
        lineTo(cx + w * 0.18f, cy - h * 0.3f)
        lineTo(cx + w * 0.4f, cy - h * 0.35f) // ear right
        lineTo(cx + w * 0.32f, cy - h * 0.2f)
        cubicTo(cx + w * 0.42f, cy + h * 0.1f, cx + w * 0.2f, cy + h * 0.35f, cx, cy + h * 0.38f)
        cubicTo(cx - w * 0.2f, cy + h * 0.35f, cx - w * 0.42f, cy + h * 0.1f, cx - w * 0.32f, cy - h * 0.2f)
        close()
    }
    drawPath(head, inkColor, alpha = alpha, blendMode = blendMode)

    // Snarl & Eyes (Negative Space / Accent)
    drawCircle(accent, radius = w * 0.04f, center = Offset(cx - w * 0.16f, cy - h * 0.08f), alpha = alpha, blendMode = blendMode)
    drawCircle(accent, radius = w * 0.04f, center = Offset(cx + w * 0.16f, cy - h * 0.08f), alpha = alpha, blendMode = blendMode)

    // Sharp Fangs
    val fangs = Path().apply {
        moveTo(cx - w * 0.14f, cy + h * 0.12f)
        lineTo(cx - w * 0.12f, cy + h * 0.25f)
        lineTo(cx - w * 0.08f, cy + h * 0.14f)
        moveTo(cx + w * 0.14f, cy + h * 0.12f)
        lineTo(cx + w * 0.12f, cy + h * 0.25f)
        lineTo(cx + w * 0.08f, cy + h * 0.14f)
    }
    drawPath(fangs, Color.White, alpha = alpha, style = Stroke(w * 0.02f), blendMode = blendMode)
}

private fun DrawScope.drawWhaleStars(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Graceful streamlined humpback whale
    val whale = Path().apply {
        moveTo(cx - w * 0.42f, cy + h * 0.05f) // snout
        cubicTo(cx - w * 0.2f, cy - h * 0.25f, cx + w * 0.2f, cy - h * 0.15f, cx + w * 0.38f, cy - h * 0.08f)
        cubicTo(cx + w * 0.44f, cy - h * 0.18f, cx + w * 0.46f, cy + h * 0.02f, cx + w * 0.36f, cy + h * 0.06f) // fluke
        cubicTo(cx + w * 0.15f, cy + h * 0.22f, cx - w * 0.2f, cy + h * 0.24f, cx - w * 0.42f, cy + h * 0.05f)
        close()
    }
    drawPath(whale, inkColor, alpha = alpha, style = Stroke(w * 0.03f, cap = StrokeCap.Round), blendMode = blendMode)

    // Belly grooves (Minimalist fine-lines)
    for (i in 1..3) {
        val y = cy + h * (0.05f + i * 0.04f)
        drawLine(
            inkColor,
            start = Offset(cx - w * 0.25f + i * 15f, y),
            end = Offset(cx + w * 0.05f - i * 10f, y),
            strokeWidth = w * 0.015f,
            alpha = alpha * 0.7f,
            blendMode = blendMode
        )
    }

    // Little constellations
    val stars = listOf(
        Offset(cx - w * 0.3f, cy - h * 0.28f),
        Offset(cx - w * 0.1f, cy - h * 0.34f),
        Offset(cx + w * 0.2f, cy - h * 0.32f),
        Offset(cx + w * 0.3f, cy + h * 0.26f)
    )
    for (star in stars) {
        drawCircle(accent, radius = w * 0.025f, center = star, alpha = alpha, blendMode = blendMode)
    }
}

private fun DrawScope.drawKitsuneFox(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Mask / face geometry
    val face = Path().apply {
        moveTo(cx, cy + h * 0.3f)
        lineTo(cx - w * 0.28f, cy)
        lineTo(cx - w * 0.25f, cy - h * 0.25f) // left ear
        lineTo(cx - w * 0.08f, cy - h * 0.12f)
        lineTo(cx + w * 0.08f, cy - h * 0.12f)
        lineTo(cx + w * 0.25f, cy - h * 0.25f) // right ear
        lineTo(cx + w * 0.28f, cy)
        close()
    }
    drawPath(face, inkColor, alpha = alpha, style = Stroke(w * 0.03f, cap = StrokeCap.Round), blendMode = blendMode)

    // Whiskers and Spirit Fire wisps
    val wisps = Path().apply {
        moveTo(cx - w * 0.35f, cy - h * 0.05f)
        cubicTo(cx - w * 0.45f, cy - h * 0.2f, cx - w * 0.2f, cy - h * 0.35f, cx, cy - h * 0.4f)
        cubicTo(cx + w * 0.2f, cy - h * 0.35f, cx + w * 0.45f, cy - h * 0.2f, cx + w * 0.35f, cy - h * 0.05f)
    }
    drawPath(wisps, accent, alpha = alpha * 0.85f, style = Stroke(w * 0.02f), blendMode = blendMode)

    // Eyes
    drawLine(accent, Offset(cx - w * 0.18f, cy), Offset(cx - w * 0.05f, cy + h * 0.04f), strokeWidth = w * 0.03f, alpha = alpha)
    drawLine(accent, Offset(cx + w * 0.18f, cy), Offset(cx + w * 0.05f, cy + h * 0.04f), strokeWidth = w * 0.03f, alpha = alpha)
}

private fun DrawScope.drawBotanicalSakura(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Branch curve
    val branch = Path().apply {
        moveTo(cx - w * 0.35f, cy + h * 0.4f)
        cubicTo(cx - w * 0.1f, cy + h * 0.15f, cx + w * 0.1f, cy - h * 0.1f, cx + w * 0.35f, cy - h * 0.38f)
    }
    drawPath(branch, inkColor, alpha = alpha, style = Stroke(w * 0.03f, cap = StrokeCap.Round), blendMode = blendMode)

    // Sakura blossoms along the branch
    val blossomCenters = listOf(
        Offset(cx - w * 0.18f, cy + h * 0.18f),
        Offset(cx, cy),
        Offset(cx + w * 0.2f, cy - h * 0.18f)
    )

    for (center in blossomCenters) {
        for (i in 0 until 5) {
            val angle = Math.toRadians((i * 72).toDouble()).toFloat()
            val px = center.x + cos(angle) * (w * 0.08f)
            val py = center.y + sin(angle) * (h * 0.08f)
            drawCircle(accent, radius = w * 0.045f, center = Offset(px, py), alpha = alpha * 0.85f, blendMode = blendMode)
        }
        drawCircle(inkColor, radius = w * 0.02f, center = center, alpha = alpha, blendMode = blendMode)
    }
}

private fun DrawScope.drawRavenRunes(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Outer Runic circle
    drawCircle(inkColor, radius = w * 0.42f, center = Offset(cx, cy), alpha = alpha, style = Stroke(w * 0.02f), blendMode = blendMode)

    // Raven silhouette (Wings outstretched)
    val raven = Path().apply {
        moveTo(cx, cy - h * 0.28f) // beak
        lineTo(cx + w * 0.4f, cy - h * 0.05f) // wing tip right
        lineTo(cx + w * 0.15f, cy + h * 0.08f)
        lineTo(cx, cy + h * 0.32f) // tail
        lineTo(cx - w * 0.15f, cy + h * 0.08f)
        lineTo(cx - w * 0.4f, cy - h * 0.05f) // wing tip left
        close()
    }
    drawPath(raven, inkColor, alpha = alpha, blendMode = blendMode)

    // Vegvisir / Helm of Awe center glyph
    drawCircle(accent, radius = w * 0.04f, center = Offset(cx, cy), alpha = alpha, blendMode = blendMode)
}

private fun DrawScope.drawButterflyGeo(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Butterfly body line
    drawLine(inkColor, Offset(cx, cy - h * 0.3f), Offset(cx, cy + h * 0.3f), strokeWidth = w * 0.025f, alpha = alpha, blendMode = blendMode)

    // Left Wing (Natural Organic)
    val leftWing = Path().apply {
        moveTo(cx, cy - h * 0.05f)
        cubicTo(cx - w * 0.45f, cy - h * 0.35f, cx - w * 0.42f, cy + h * 0.05f, cx, cy + h * 0.02f)
        cubicTo(cx - w * 0.35f, cy + h * 0.12f, cx - w * 0.28f, cy + h * 0.32f, cx, cy + h * 0.18f)
    }
    drawPath(leftWing, inkColor, alpha = alpha, style = Stroke(w * 0.02f), blendMode = blendMode)

    // Right Wing (Sacred Geometric Polygon)
    val rightWing = Path().apply {
        moveTo(cx, cy - h * 0.05f)
        lineTo(cx + w * 0.38f, cy - h * 0.25f)
        lineTo(cx + w * 0.42f, cy - h * 0.05f)
        lineTo(cx + w * 0.22f, cy + h * 0.05f)
        lineTo(cx + w * 0.3f, cy + h * 0.28f)
        lineTo(cx, cy + h * 0.18f)
    }
    drawPath(rightWing, accent, alpha = alpha, style = Stroke(w * 0.02f), blendMode = blendMode)
}

private fun DrawScope.drawTrashSamurai(
    w: Float, h: Float,
    inkColor: Color, redColor: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Bold blood red brush stroke / circle background
    drawCircle(redColor, radius = w * 0.35f, center = Offset(cx, cy), alpha = alpha * 0.85f, blendMode = blendMode)

    // Dynamic diagonal slash lines
    drawLine(Color.White, Offset(cx - w * 0.4f, cy - h * 0.2f), Offset(cx + w * 0.4f, cy + h * 0.2f), strokeWidth = w * 0.035f, alpha = alpha)

    // Samurai Kabuto Helmet outline
    val kabuto = Path().apply {
        moveTo(cx, cy - h * 0.35f) // crest peak
        lineTo(cx - w * 0.3f, cy - h * 0.1f)
        lineTo(cx - w * 0.2f, cy + h * 0.25f)
        lineTo(cx, cy + h * 0.32f)
        lineTo(cx + w * 0.2f, cy + h * 0.25f)
        lineTo(cx + w * 0.3f, cy - h * 0.1f)
        close()
    }
    drawPath(kabuto, inkColor, alpha = alpha, style = Stroke(w * 0.03f), blendMode = blendMode)
}

private fun DrawScope.drawTarotSun(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // Card frame
    drawRect(
        inkColor,
        topLeft = Offset(cx - w * 0.38f, cy - h * 0.44f),
        size = Size(w * 0.76f, h * 0.88f),
        style = Stroke(w * 0.02f),
        alpha = alpha,
        blendMode = blendMode
    )

    // Inner radiant sun
    drawCircle(accent, radius = w * 0.16f, center = Offset(cx, cy - h * 0.05f), alpha = alpha, blendMode = blendMode)

    // Sunburst rays
    for (i in 0 until 12) {
        val angle = Math.toRadians((i * 30).toDouble()).toFloat()
        val r1 = w * 0.18f
        val r2 = w * 0.26f
        val x1 = cx + cos(angle) * r1
        val y1 = (cy - h * 0.05f) + sin(angle) * r1
        val x2 = cx + cos(angle) * r2
        val y2 = (cy - h * 0.05f) + sin(angle) * r2
        drawLine(inkColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = w * 0.015f, alpha = alpha, blendMode = blendMode)
    }

    // "THE SUN" minimal bar
    drawLine(inkColor, Offset(cx - w * 0.25f, cy + h * 0.32f), Offset(cx + w * 0.25f, cy + h * 0.32f), strokeWidth = w * 0.02f, alpha = alpha)
}

private fun DrawScope.drawAiCustomDesign(
    w: Float, h: Float,
    inkColor: Color, accent: Color,
    blendMode: BlendMode, alpha: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    // AI sacred glyph & neural geometric aura
    drawCircle(accent, radius = w * 0.35f, center = Offset(cx, cy), alpha = alpha * 0.5f, style = Stroke(w * 0.015f), blendMode = blendMode)
    drawCircle(inkColor, radius = w * 0.22f, center = Offset(cx, cy), alpha = alpha, style = Stroke(w * 0.025f), blendMode = blendMode)

    val diamond = Path().apply {
        moveTo(cx, cy - h * 0.38f)
        lineTo(cx + w * 0.32f, cy)
        lineTo(cx, cy + h * 0.38f)
        lineTo(cx - w * 0.32f, cy)
        close()
    }
    drawPath(diamond, inkColor, alpha = alpha, style = Stroke(w * 0.02f), blendMode = blendMode)

    // Center jewel
    drawCircle(accent, radius = w * 0.06f, center = Offset(cx, cy), alpha = alpha, blendMode = blendMode)
}
