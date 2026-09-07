package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BadgeType
import com.example.ui.theme.SystemThemeColors
import kotlin.random.Random

private data class ConfettiParticle(
    val xRatio: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float
)

@Composable
fun BadgeCelebrationDialog(
    badge: BadgeType,
    sysColors: SystemThemeColors,
    onDismiss: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(badge) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Generate 50 confetti particles
    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD700), Color(0xFFFF4081), Color(0xFF00E5FF),
            Color(0xFF76FF03), Color(0xFFE040FB), Color(0xFFFF9100)
        )
        val rnd = Random(42)
        List(55) {
            ConfettiParticle(
                xRatio = rnd.nextFloat(),
                initialY = -rnd.nextFloat() * 200f,
                speed = 280f + rnd.nextFloat() * 450f,
                size = 10f + rnd.nextFloat() * 14f,
                color = colors[rnd.nextInt(colors.size)],
                rotationSpeed = 120f + rnd.nextFloat() * 360f
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f)),
                contentAlignment = Alignment.Center
            ) {
                // Confetti Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val p = progress.value
                    particles.forEach { particle ->
                        val currentY = particle.initialY + (particle.speed * p * (size.height / 350f))
                        val currentX = particle.xRatio * size.width + (kotlin.math.sin(p * 8f + particle.xRatio * 10f) * 30f)
                        if (currentY < size.height + 40f) {
                            rotate(
                                degrees = p * particle.rotationSpeed,
                                pivot = Offset(currentX, currentY)
                            ) {
                                drawRect(
                                    color = particle.color,
                                    topLeft = Offset(currentX, currentY),
                                    size = Size(particle.size, particle.size * 0.55f)
                                )
                            }
                        }
                    }
                }

                // Glassmorphic Modal Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(20.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = sysColors.surface.copy(alpha = 0.94f)),
                    border = BorderStroke(1.5.dp, Color(badge.accentColorHex).copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(badge.accentColorHex).copy(alpha = 0.22f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Pulsing Badge Glow Icon
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(96.dp)
                                    .scale(pulseScale)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    Color(badge.accentColorHex).copy(alpha = 0.5f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = sysColors.surface,
                                    border = BorderStroke(2.dp, Color(badge.accentColorHex)),
                                    modifier = Modifier.size(76.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = badge.iconEmoji,
                                            fontSize = 38.sp
                                        )
                                    }
                                }
                            }

                            // Header Tags
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = Color(badge.accentColorHex).copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, Color(badge.accentColorHex).copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "🎉 نشان افتخار جدید بازگشایی شد!",
                                    color = Color(badge.accentColorHex),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }

                            // Badge Title
                            Text(
                                text = badge.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = sysColors.text,
                                textAlign = TextAlign.Center
                            )

                            // Badge Description
                            Text(
                                text = badge.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = sysColors.textMuted,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Confirm Button
                            Button(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(badge.accentColorHex)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = "بسیار عالی! 🏆",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
