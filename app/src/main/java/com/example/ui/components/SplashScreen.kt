package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Ultra-sleek, luxurious and minimal Splash Screen
 * Featuring 3D Glassmorphic AppLogo with soft ambient glow pulse,
 * Champagne gold typography, and a refined minimal loading track.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    var isStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isStarted = true
    }

    // Intro Scale & Fade Animations
    val contentAlpha by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "splash_fade"
    )

    val contentScale by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0.90f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "splash_scale"
    )

    // Infinite Ambient Halo Glow Pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Shimmer bar progress
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_bar"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0F1D33), // Deep Navy Core
                            Color(0xFF070D18), // Rich Dark Canvas
                            Color(0xFF040810)  // Obsidian Edge
                        ),
                        center = Offset(0.5f, 0.42f)
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Empty Spacer
                Spacer(modifier = Modifier.height(32.dp))

                // Center Brand Core
                Column(
                    modifier = Modifier
                        .scale(contentScale)
                        .alpha(contentAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    // Logo Box with Glowing Halo Ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        // Ambient Radial Glow Pulse
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .scale(glowScale)
                                .alpha(glowAlpha)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700).copy(alpha = 0.45f),
                                            Color(0xFF38BDF8).copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // 3D Glassmorphic App Brand Icon
                        AppLogo(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFFFFD700).copy(alpha = 0.35f)),
                            shapeRadius = 26.dp,
                            elevation = 8.dp
                        )
                    }

                    // Main App Name & Champagne Typography
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "نرم‌افزار رمان‌خوان روایت ما",
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFBF6EC),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.shadow(4.dp, shape = RoundedCornerShape(4.dp), spotColor = Color(0x66000000))
                        )

                        // Gold Accent Pill Subtitle
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color(0x22F59E0B),
                            border = BorderStroke(1.dp, Color(0x44F59E0B))
                        ) {
                            Text(
                                text = "«تجربه‌ای نو و اختصاصی در خواندن رمان»",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFFE082),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Bottom Minimal Progress Loader & Publisher Tag
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(contentAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sleek Modern Loading Track
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(3.5.dp)
                            .clip(CircleShape)
                            .background(Color(0x26FFFFFF))
                    ) {
                        val startFraction = (shimmerOffset - 0.3f).coerceIn(0f, 1f)
                        val endFraction = (shimmerOffset + 0.3f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(endFraction)
                                .height(3.5.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0x00FFD700),
                                            Color(0xFFFFD700),
                                            Color(0xFF38BDF8),
                                            Color(0x0038BDF8)
                                        )
                                    )
                                )
                        )
                    }

                    // Version & Channel Tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                        )
                        Text(
                            text = "توسعه‌یافته برای کانال رسمی روایت ما",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}
