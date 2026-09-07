package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ReadingStatsData
import com.example.ui.theme.SystemThemeColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

private fun toPersianNumber(num: Any): String {
    val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    return num.toString().map { ch ->
        if (ch in '0'..'9') persianDigits[ch - '0'] else ch
    }.joinToString("")
}

data class DayStat(
    val dayName: String,
    val dateKey: String,
    val minutes: Int,
    val isToday: Boolean
)

@Composable
fun ReadingStatsDialog(
    stats: ReadingStatsData,
    sysColors: SystemThemeColors,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame_anim")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    // Today's Date
    val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val todayMinutes = stats.dailyMinutesMap[todayKey] ?: 0

    // Build the 7 days of the week (Saturday to Friday)
    val weekDays = buildWeeklyData(stats.dailyMinutesMap, todayKey)
    val maxMinutesInWeek = max(60, weekDays.maxOfOrNull { it.minutes } ?: 60)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("reading_stats_screen"),
                color = sysColors.bg
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Bar
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
                                    color = sysColors.primary.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, sysColors.primary.copy(alpha = 0.4f)),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.BarChart,
                                            contentDescription = null,
                                            tint = sysColors.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "آمار و عملکرد مطالعه",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = sysColors.text
                                    )
                                    Text(
                                        text = "ردیاب هوشمند زمان مطالعه و استریک",
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

                    // Main Scrollable Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Streak Hero Card with Animated Flame
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = sysColors.surface),
                            border = BorderStroke(1.5.dp, Color(0xFFF97316).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFF97316).copy(alpha = 0.18f),
                                                Color(0xFFEF4444).copy(alpha = 0.08f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(50.dp),
                                            color = Color(0xFFF97316).copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.4f))
                                        ) {
                                            Text(
                                                text = "🔥 استریک مطالعه روزانه",
                                                color = Color(0xFFF97316),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        Text(
                                            text = if (stats.currentStreakDays > 0) {
                                                "${toPersianNumber(stats.currentStreakDays)} روز پیوسته"
                                            } else {
                                                "امروز شروع کنید!"
                                            },
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black,
                                            color = sysColors.text
                                        )

                                        Text(
                                            text = if (todayMinutes >= 5) {
                                                "✅ هدف مطالعه امروز (حداقل ۵ دقیقه) تکمیل شد!"
                                            } else {
                                                "⏱️ حداقل ۵ دقیقه امروز بخوانید تا استریک شما ثبت شود."
                                            },
                                            fontSize = 12.sp,
                                            color = if (todayMinutes >= 5) Color(0xFF10B981) else sysColors.textMuted
                                        )
                                    }

                                    // Pulsing Flame Container
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(76.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(76.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        colors = listOf(
                                                            Color(0xFFF97316).copy(alpha = 0.35f),
                                                            Color.Transparent
                                                        )
                                                    )
                                                )
                                        )
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF97316).copy(alpha = 0.15f),
                                            border = BorderStroke(1.5.dp, Color(0xFFF97316)),
                                            modifier = Modifier.size(60.dp)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LocalFireDepartment,
                                                    contentDescription = "آتش استریک",
                                                    tint = Color(0xFFF97316),
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .scale(flameScale)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Metrics 2x2 Grid Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Schedule,
                                iconColor = Color(0xFF38BDF8),
                                title = "مجموع ساعات",
                                value = if (stats.totalReadingHours > 0) {
                                    "${toPersianNumber(stats.totalReadingHours)} س و ${toPersianNumber(stats.remainingReadingMinutes)} د"
                                } else {
                                    "${toPersianNumber(stats.totalReadingMinutes)} دقیقه"
                                },
                                subtitle = "کل زمان خوانش رمان",
                                sysColors = sysColors
                            )

                            MetricStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Today,
                                iconColor = Color(0xFF10B981),
                                title = "مطالعه امروز",
                                value = "${toPersianNumber(todayMinutes)} دقیقه",
                                subtitle = "فعال در صفحه رمان",
                                sysColors = sysColors
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.BookmarkBorder,
                                iconColor = Color(0xFFA855F7),
                                title = "قسمت‌های امروز",
                                value = "${toPersianNumber(stats.chaptersCompletedToday.size)} قسمت",
                                subtitle = "پیشرفت روزانه",
                                sysColors = sysColors
                            )

                            MetricStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.BarChart,
                                iconColor = Color(0xFFF59E0B),
                                title = "میانگین روزانه",
                                value = "${toPersianNumber(if (stats.dailyMinutesMap.isNotEmpty()) stats.totalReadingMinutes / stats.dailyMinutesMap.size else todayMinutes)} د",
                                subtitle = "بر اساس روزهای مطالعه",
                                sysColors = sysColors
                            )
                        }

                        // 3. Weekly Reading Bar Chart (شنبه تا جمعه)
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = sysColors.surface),
                            border = BorderStroke(1.dp, sysColors.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
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
                                            imageVector = Icons.Default.BarChart,
                                            contentDescription = null,
                                            tint = sysColors.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "نمودار هفتگی مطالعه",
                                            fontWeight = FontWeight.Bold,
                                            color = sysColors.text,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Text(
                                        text = "شنبه تا جمعه",
                                        fontSize = 11.sp,
                                        color = sysColors.textMuted
                                    )
                                }

                                // Chart Graphic Container
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(sysColors.surfaceGlass)
                                        .border(BorderStroke(1.dp, sysColors.border.copy(alpha = 0.5f)), RoundedCornerShape(14.dp))
                                        .padding(horizontal = 12.dp, vertical = 14.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        weekDays.forEach { day ->
                                            val barFraction = (day.minutes.toFloat() / maxMinutesInWeek).coerceIn(0.04f, 1f)
                                            val barColor = if (day.isToday) sysColors.accent else sysColors.primary

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Bottom,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                // Minutes tooltip on top of bar
                                                Text(
                                                    text = if (day.minutes > 0) toPersianNumber(day.minutes) else "-",
                                                    fontSize = 10.sp,
                                                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (day.isToday) sysColors.accent else sysColors.textMuted
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                // Vertical Bar
                                                Box(
                                                    modifier = Modifier
                                                        .width(18.dp)
                                                        .fillMaxHeight(barFraction * 0.72f)
                                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                        .background(
                                                            Brush.verticalGradient(
                                                                colors = if (day.isToday) {
                                                                    listOf(sysColors.accent, Color(0xFF38BDF8))
                                                                } else if (day.minutes > 0) {
                                                                    listOf(barColor, barColor.copy(alpha = 0.4f))
                                                                } else {
                                                                    listOf(sysColors.border, sysColors.border.copy(alpha = 0.3f))
                                                                }
                                                            )
                                                        )
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                // Day Label
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (day.isToday) sysColors.accent.copy(alpha = 0.2f) else Color.Transparent
                                                ) {
                                                    Text(
                                                        text = day.dayName,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.Medium,
                                                        color = if (day.isToday) sysColors.accent else sysColors.textMuted,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Offline Smart Tracker Explanation Banner
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = sysColors.surfaceGlass,
                            border = BorderStroke(1.dp, sysColors.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Text(
                                    text = "💡 ردیاب زمان تنها هنگام حضور شما درون متن رمان فعال است. با قفل صفحه یا خروج از برنامه، زمان‌سنج به صورت خودکار متوقف می‌گردد.",
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = sysColors.textMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    subtitle: String,
    sysColors: SystemThemeColors
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = sysColors.surface),
        border = BorderStroke(1.dp, sysColors.border),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconColor.copy(alpha = 0.16f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = sysColors.textMuted
                )
            }

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = sysColors.text
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = sysColors.textMuted
            )
        }
    }
}

private fun buildWeeklyData(dailyMap: Map<String, Int>, todayKey: String): List<DayStat> {
    val dayNames = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
    val cal = Calendar.getInstance()
    // Align to the most recent Saturday
    // In Java Calendar, Saturday = Calendar.SATURDAY (7)
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val diffToSat = when (dayOfWeek) {
        Calendar.SATURDAY -> 0
        Calendar.SUNDAY -> 1
        Calendar.MONDAY -> 2
        Calendar.TUESDAY -> 3
        Calendar.WEDNESDAY -> 4
        Calendar.THURSDAY -> 5
        Calendar.FRIDAY -> 6
        else -> 0
    }
    cal.add(Calendar.DAY_OF_YEAR, -diffToSat)

    val list = mutableListOf<DayStat>()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    for (i in 0 until 7) {
        val dateStr = sdf.format(cal.time)
        val mins = dailyMap[dateStr] ?: 0
        list.add(
            DayStat(
                dayName = dayNames[i],
                dateKey = dateStr,
                minutes = mins,
                isToday = (dateStr == todayKey)
            )
        )
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }

    return list
}
