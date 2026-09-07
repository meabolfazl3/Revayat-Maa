package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BadgeType
import com.example.data.model.ReadingStatsData
import com.example.ui.theme.SystemThemeColors

private fun persianDigits(num: Any): String {
    val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    return num.toString().map { ch ->
        if (ch in '0'..'9') persianDigits[ch - '0'] else ch
    }.joinToString("")
}

@Composable
fun HallOfFameDialog(
    stats: ReadingStatsData,
    sysColors: SystemThemeColors,
    onDismiss: () -> Unit
) {
    val allBadges = BadgeType.values().toList()
    val unlockedCount = stats.unlockedBadges.size
    val totalBadges = allBadges.size
    val progressRatio = if (totalBadges > 0) unlockedCount.toFloat() / totalBadges else 0f

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("hall_of_fame_screen"),
                color = sysColors.bg
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top App Bar
                    Surface(
                        color = sysColors.surface.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, sysColors.border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFF59E0B).copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "اتاق افتخارات",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = sysColors.text
                                    )
                                    Text(
                                        text = "مدال‌ها و دستاوردهای مطالعه رمان",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = sysColors.textMuted
                                    )
                                }
                            }

                            // Back Button
                            Surface(
                                shape = CircleShape,
                                color = sysColors.surfaceGlass,
                                border = BorderStroke(1.dp, sysColors.border),
                                modifier = Modifier.size(38.dp)
                            ) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "بازگشت",
                                        tint = sysColors.text,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Content Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header Summary Banner Span Full Width
                        item(span = { GridItemSpan(2) }) {
                            Card(
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = sysColors.surface),
                                border = BorderStroke(1.dp, sysColors.border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFFF59E0B).copy(alpha = 0.15f),
                                                    Color(0xFF8B5CF6).copy(alpha = 0.10f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                        .padding(18.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Stars,
                                                    contentDescription = null,
                                                    tint = Color(0xFFF59E0B),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    text = "پیشرفت کل مدال‌ها",
                                                    fontWeight = FontWeight.Bold,
                                                    color = sysColors.text,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(50.dp),
                                                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                                border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                                            ) {
                                                Text(
                                                    text = "${persianDigits(unlockedCount)} از ${persianDigits(totalBadges)} نشان",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFF59E0B),
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        // Progress Bar
                                        LinearProgressIndicator(
                                            progress = { progressRatio },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = Color(0xFFF59E0B),
                                            trackColor = sysColors.border
                                        )

                                        // Theme Unlock Progression Hint
                                        val themeHintText = when {
                                            unlockedCount >= 12 -> "👑 تبریک! تمام پوسته‌های ویژه از جمله «زرشکی سلطنتی» بازگشایی شدند!"
                                            unlockedCount >= 8 -> "✨ پوسته‌های شنی مخملی، شفق قطبی، سبز پاستلی و صبح مینیمال باز هستند! (با ۱۲ امتیاز: «زرشکی سلطنتی» 👑)"
                                            unlockedCount >= 6 -> "✨ پوسته‌های سبز پاستلی، یشم، صبح مینیمال و نئون بنفش باز شدند! (با ۸ امتیاز: «شنی مخملی» 🏖️)"
                                            unlockedCount >= 5 -> "✨ پوسته‌های یشم، صبح مینیمال و نئون بنفش باز شدند! (با ۶ امتیاز: «سبز پاستلی» 🌿)"
                                            unlockedCount >= 4 -> "✨ پوسته صبح مینیمال باز شد! (با ۵ امتیاز: «یشم امپراتوری» 🎋)"
                                            unlockedCount >= 2 -> "✨ پوسته نئون بنفش باز شد! (با ۴ امتیاز: «صبح مینیمال» ☀️)"
                                            else -> "🔒 با کسب ۲ امتیاز، اولین پوسته ویژه «نئون بنفش سایبرپانک» باز می‌شود!"
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFA855F7).copy(alpha = 0.14f),
                                            border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.35f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = themeHintText,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (unlockedCount >= 2) Color(0xFFC084FC) else sysColors.textMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2-Column Badges Grid
                        items(allBadges) { badge ->
                            val isUnlocked = stats.unlockedBadges.contains(badge.id)
                            val badgeColor = Color(badge.accentColorHex)

                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUnlocked) {
                                        sysColors.surface.copy(alpha = if (sysColors.isDark) 0.95f else 0.95f)
                                    } else {
                                        sysColors.surface.copy(alpha = if (sysColors.isDark) 0.55f else 0.70f)
                                    }
                                ),
                                border = BorderStroke(
                                    width = if (isUnlocked) 1.5.dp else 1.dp,
                                    color = if (isUnlocked) badgeColor.copy(alpha = glowAlpha) else sysColors.border.copy(alpha = 0.6f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 3.dp else 1.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("badge_card_${badge.id}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            if (isUnlocked) {
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        badgeColor.copy(alpha = if (sysColors.isDark) 0.18f else 0.08f),
                                                        Color.Transparent
                                                    )
                                                )
                                            } else {
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        (if (sysColors.isDark) Color.White else sysColors.border).copy(alpha = 0.03f),
                                                        Color.Transparent
                                                    )
                                                )
                                            }
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Status Pill (Top)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            if (isUnlocked) {
                                                Surface(
                                                    shape = RoundedCornerShape(50.dp),
                                                    color = badgeColor.copy(alpha = 0.18f),
                                                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = badgeColor,
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                        Text(
                                                            text = "کسب شده",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = badgeColor
                                                        )
                                                    }
                                                }
                                            } else {
                                                Surface(
                                                    shape = RoundedCornerShape(50.dp),
                                                    color = if (sysColors.isDark) Color.Black.copy(alpha = 0.25f) else sysColors.textMuted.copy(alpha = 0.12f),
                                                    border = BorderStroke(1.dp, sysColors.border.copy(alpha = 0.5f))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Lock,
                                                            contentDescription = null,
                                                            tint = sysColors.textMuted,
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                        Text(
                                                            text = "قفل",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            color = sysColors.textMuted
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Badge Icon Center
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.size(56.dp)
                                        ) {
                                            if (isUnlocked) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            Brush.radialGradient(
                                                                colors = listOf(
                                                                    badgeColor.copy(alpha = 0.4f),
                                                                    Color.Transparent
                                                                )
                                                            )
                                                        )
                                                )
                                            }

                                            Surface(
                                                shape = CircleShape,
                                                color = if (isUnlocked) badgeColor.copy(alpha = 0.15f) else (if (sysColors.isDark) Color.White.copy(alpha = 0.05f) else sysColors.bg.copy(alpha = 0.7f)),
                                                border = BorderStroke(
                                                    1.5.dp,
                                                    if (isUnlocked) badgeColor else sysColors.border.copy(alpha = 0.6f)
                                                ),
                                                modifier = Modifier.size(50.dp)
                                            ) {
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    Text(
                                                        text = if (isUnlocked) badge.iconEmoji else "🔒",
                                                        fontSize = 24.sp
                                                    )
                                                }
                                            }
                                        }

                                        // Title
                                        Text(
                                            text = badge.title,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.5.sp,
                                            color = if (isUnlocked) sysColors.text else sysColors.textMuted,
                                            textAlign = TextAlign.Center
                                        )

                                        // Description
                                        Text(
                                            text = badge.description,
                                            fontSize = 11.5.sp,
                                            lineHeight = 17.sp,
                                            color = sysColors.textMuted,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.height(44.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Spacer
                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }
}
