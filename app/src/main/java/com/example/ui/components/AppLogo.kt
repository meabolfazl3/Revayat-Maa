package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppLogo(
    modifier: Modifier = Modifier.size(48.dp),
    shapeRadius: Dp = 14.dp,
    elevation: Dp = 0.dp
) {
    Box(
        modifier = modifier
            .then(if (elevation > 0.dp) Modifier.shadow(elevation, RoundedCornerShape(shapeRadius)) else Modifier)
            .clip(RoundedCornerShape(shapeRadius))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFFAF7F2),
                        Color(0xFFEFE7DB)
                    ),
                    center = Offset(0.35f, 0.35f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawAppBrandLogo(this)
        }
    }
}

private fun drawAppBrandLogo(drawScope: DrawScope) {
    val w = drawScope.size.width
    val h = drawScope.size.height

    fun sx(x: Float): Float = (x / 100f) * w
    fun sy(y: Float): Float = (y / 100f) * h

    // 1. Golden Bottom Book Covers / Trims (underneath both pages)
    val leftGoldTrim = Path().apply {
        moveTo(sx(21f), sy(68f))
        cubicTo(sx(21f), sy(72f), sx(32f), sy(74f), sx(48f), sy(80f))
        lineTo(sx(48f), sy(82f))
        cubicTo(sx(32f), sy(76f), sx(21f), sy(74f), sx(21f), sy(70f))
        close()
    }
    drawScope.drawPath(
        path = leftGoldTrim,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFF3CF7A), Color(0xFFC78F23)),
            start = Offset(sx(21f), sy(68f)),
            end = Offset(sx(48f), sy(82f))
        ),
        style = Fill
    )

    val rightGoldTrim = Path().apply {
        moveTo(sx(52f), sy(80f))
        cubicTo(sx(68f), sy(74f), sx(79f), sy(72f), sx(79f), sy(68f))
        lineTo(sx(79f), sy(70f))
        cubicTo(sx(79f), sy(74f), sx(68f), sy(76f), sx(52f), sy(82f))
        close()
    }
    drawScope.drawPath(
        path = rightGoldTrim,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFC78F23), Color(0xFFF3CF7A)),
            start = Offset(sx(52f), sy(80f)),
            end = Offset(sx(79f), sy(68f))
        ),
        style = Fill
    )

    // Center Gold Spine Base
    val goldSpine = Path().apply {
        moveTo(sx(47f), sy(79f))
        lineTo(sx(50f), sy(82.5f))
        lineTo(sx(53f), sy(79f))
        cubicTo(sx(51f), sy(81f), sx(49f), sy(81f), sx(47f), sy(79f))
        close()
    }
    drawScope.drawPath(
        path = goldSpine,
        color = Color(0xFFDCA12D),
        style = Fill
    )

    // 2. Left Navy Page (Solid rich deep navy with subtle gradient)
    val leftNavyPage = Path().apply {
        moveTo(sx(23f), sy(35f))
        cubicTo(sx(32f), sy(38f), sx(43f), sy(44f), sx(49f), sy(52f))
        lineTo(sx(49f), sy(79f))
        cubicTo(sx(43f), sy(72f), sx(32f), sy(66f), sx(23f), sy(67f))
        close()
    }
    drawScope.drawPath(
        path = leftNavyPage,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF0D254C), Color(0xFF081833), Color(0xFF040D1D)),
            start = Offset(sx(23f), sy(35f)),
            end = Offset(sx(49f), sy(79f))
        ),
        style = Fill
    )

    // Left Page Subtle Inner Edge Highlight
    drawScope.drawPath(
        path = leftNavyPage,
        color = Color(0xFF1E457E),
        style = Stroke(width = (w * 0.015f).coerceAtLeast(1f))
    )

    // 3. Right Underneath Navy Page
    val rightNavyPageBase = Path().apply {
        moveTo(sx(77f), sy(41f))
        cubicTo(sx(68f), sy(38f), sx(57f), sy(44f), sx(51f), sy(52f))
        lineTo(sx(51f), sy(79f))
        cubicTo(sx(57f), sy(72f), sx(68f), sy(66f), sx(77f), sy(67f))
        close()
    }
    drawScope.drawPath(
        path = rightNavyPageBase,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF0D254C), Color(0xFF081833), Color(0xFF040D1D)),
            start = Offset(sx(77f), sy(41f)),
            end = Offset(sx(51f), sy(79f))
        ),
        style = Fill
    )

    // 4. Right Glossy / Glass Upper Page (with glass transparency & gold trim)
    val rightGlassPage = Path().apply {
        moveTo(sx(73f), sy(35f))
        cubicTo(sx(65f), sy(37f), sx(56f), sy(43f), sx(51f), sy(51f))
        lineTo(sx(51f), sy(78f))
        cubicTo(sx(56f), sy(71f), sx(65f), sy(65f), sx(73f), sy(67f))
        close()
    }

    // Glass page body
    drawScope.drawPath(
        path = rightGlassPage,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xEE1E4070),
                Color(0xDD122A50),
                Color(0xCC0C1E3C)
            ),
            start = Offset(sx(73f), sy(35f)),
            end = Offset(sx(51f), sy(78f))
        ),
        style = Fill
    )

    // Right Glass Page Gold Rim
    drawScope.drawPath(
        path = rightGlassPage,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFDE4A6), Color(0xFFD49C2B), Color(0xFF9E6B15)),
            start = Offset(sx(73f), sy(35f)),
            end = Offset(sx(51f), sy(78f))
        ),
        style = Stroke(width = (w * 0.02f).coerceAtLeast(1.2f))
    )

    // Specular Glass Reflection Sheen on Right Page
    val glassSheen = Path().apply {
        moveTo(sx(68f), sy(36f))
        cubicTo(sx(64f), sy(38f), sx(58f), sy(43f), sx(55f), sy(49f))
        lineTo(sx(52f), sy(71f))
        cubicTo(sx(57f), sy(57f), sx(63f), sy(45f), sx(68f), sy(36f))
        close()
    }
    drawScope.drawPath(
        path = glassSheen,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0x99FFFFFF),
                Color(0x33FFFFFF),
                Color(0x00FFFFFF)
            ),
            start = Offset(sx(68f), sy(36f)),
            end = Offset(sx(52f), sy(71f))
        ),
        style = Fill
    )

    // 5. Golden 4-point Sparkle Star (top-left of quill)
    val starCenter = Offset(sx(41.5f), sy(32.5f))
    val starR = w * 0.042f
    val starPath = Path().apply {
        moveTo(starCenter.x, starCenter.y - starR)
        cubicTo(
            starCenter.x + starR * 0.15f, starCenter.y - starR * 0.15f,
            starCenter.x + starR * 0.15f, starCenter.y - starR * 0.15f,
            starCenter.x + starR, starCenter.y
        )
        cubicTo(
            starCenter.x + starR * 0.15f, starCenter.y + starR * 0.15f,
            starCenter.x + starR * 0.15f, starCenter.y + starR * 0.15f,
            starCenter.x, starCenter.y + starR
        )
        cubicTo(
            starCenter.x - starR * 0.15f, starCenter.y + starR * 0.15f,
            starCenter.x - starR * 0.15f, starCenter.y + starR * 0.15f,
            starCenter.x - starR, starCenter.y
        )
        cubicTo(
            starCenter.x - starR * 0.15f, starCenter.y - starR * 0.15f,
            starCenter.x - starR * 0.15f, starCenter.y - starR * 0.15f,
            starCenter.x, starCenter.y - starR
        )
        close()
    }
    drawScope.drawPath(
        path = starPath,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF7DB), Color(0xFFEAB744), Color(0xFFCA891C)),
            center = starCenter,
            radius = starR
        ),
        style = Fill
    )

    // 6. Majestic Golden Quill Feather (Hovering in center, emerging from spine)
    val featherPath = Path().apply {
        // Base / stem tip
        moveTo(sx(48.5f), sy(55f))
        // Left curve upwards
        cubicTo(sx(47f), sy(48f), sx(47.5f), sy(37f), sx(52f), sy(26f))
        cubicTo(sx(55f), sy(19f), sx(60f), sy(14f), sx(64.5f), sy(15f))
        // Top right tip curving down
        cubicTo(sx(64.5f), sy(17f), sx(63f), sy(22f), sx(60.5f), sy(27f))
        // Cut-in feather notch
        cubicTo(sx(58.5f), sy(30f), sx(57f), sy(31f), sx(57.5f), sy(33f))
        cubicTo(sx(61f), sy(32f), sx(63f), sy(30f), sx(64f), sy(29f))
        cubicTo(sx(63f), sy(34f), sx(60f), sy(39f), sx(56f), sy(44f))
        cubicTo(sx(52.5f), sy(49f), sx(50f), sy(53f), sx(48.5f), sy(55f))
        close()
    }

    drawScope.drawPath(
        path = featherPath,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF2D0),
                Color(0xFFF5CE6F),
                Color(0xFFE5A934),
                Color(0xFFB87816)
            ),
            start = Offset(sx(64.5f), sy(15f)),
            end = Offset(sx(48.5f), sy(55f))
        ),
        style = Fill
    )

    // Quill Feather Spine Highlight Line
    val featherSpine = Path().apply {
        moveTo(sx(48.5f), sy(55f))
        cubicTo(sx(49.5f), sy(45f), sx(53f), sy(32f), sx(62f), sy(17f))
    }
    drawScope.drawPath(
        path = featherSpine,
        color = Color(0xFFFFF8E7),
        style = Stroke(
            width = (w * 0.018f).coerceAtLeast(1.2f),
            cap = StrokeCap.Round
        )
    )
}

