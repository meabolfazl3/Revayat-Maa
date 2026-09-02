package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modern Glassmorphism Cover for Novel "The Secret of Diamonds" / «راز الماس»
 * Features:
 * - Deep navy gradient canvas with subtle grid and star-like texture
 * - Glassmorphic frosted panel
 * - Glowing Diamond / Book center insignia with breathing neon cyan/blue aura
 * - Elegant Persian and Latin typography
 */
@Composable
fun BookCoverArt(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF060913),
                        Color(0xFF0B132B),
                        Color(0xFF1C2541),
                        Color(0xFF0F172A),
                        Color(0xFF050811)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.6f),
                        Color(0xFF818CF8).copy(alpha = 0.25f),
                        Color(0xFF0284C7).copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF38BDF8).copy(alpha = 0.35f))
    ) {
        // 1. Ambient Background Light Rays & Sparkles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top soft cyan moonlight aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.22f * glowAlpha),
                        Color(0xFF6366F1).copy(alpha = 0.12f * glowAlpha),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = w * 0.75f * glowScale
                )
            )

            // Deep background accent stars
            val stars = listOf(
                Offset(w * 0.18f, h * 0.15f),
                Offset(w * 0.82f, h * 0.20f),
                Offset(w * 0.25f, h * 0.78f),
                Offset(w * 0.75f, h * 0.82f),
                Offset(w * 0.12f, h * 0.52f),
                Offset(w * 0.88f, h * 0.58f)
            )
            stars.forEachIndexed { idx, pt ->
                drawCircle(
                    color = Color(0xFFBAE6FD).copy(alpha = if (idx % 2 == 0) 0.65f * glowAlpha else 0.4f),
                    radius = (1.2 + (idx % 2)).dp.toPx(),
                    center = pt
                )
            }
        }

        // 2. Central Content Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Category & Decorative Sparkle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x330284C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6638BDF8))
                ) {
                    Text(
                        text = "رمان اختصاصی",
                        color = Color(0xFFE0F2FE),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8).copy(alpha = glowAlpha),
                    modifier = Modifier.size(15.dp)
                )
            }

            // Center Section: Glowing Diamond Insignia with Glassmorphic Plate
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Glassmorphism Diamond Emblem Card
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0x3338BDF8),
                                    Color(0x1A1E293B),
                                    Color(0x400284C7)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0x99BAE6FD),
                                    Color(0x3338BDF8),
                                    Color(0x80818CF8)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .shadow(14.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF38BDF8).copy(alpha = 0.5f))
                ) {
                    // Outer pulsing halo
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF38BDF8).copy(alpha = 0.45f * glowAlpha),
                                        Color(0xFF818CF8).copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Brilliant Diamond Icon
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "الماس",
                        tint = Color(0xFFE0F2FE),
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(8.dp, spotColor = Color(0xFF38BDF8))
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Title: «راز الماس» (Persian Titr Display Typography)
                Text(
                    text = "راز الماس",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.shadow(10.dp, spotColor = Color(0xFF38BDF8))
                )

                // Subtitle English: "THE SECRET OF DIAMONDS"
                Text(
                    text = "THE SECRET OF DIAMONDS",
                    color = Color(0xFFBAE6FD),
                    fontSize = 8.sp,
                    letterSpacing = 1.8.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Section: Author Name Plate
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0x2B0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3364748B)),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = "اثر ابوالفضل پورنجف",
                    color = Color(0xFFCBD5E1),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.5.dp)
                )
            }
        }
    }
}
