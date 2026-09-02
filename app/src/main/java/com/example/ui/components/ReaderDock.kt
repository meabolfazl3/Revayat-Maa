package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SystemThemeColors

@Composable
fun ReaderDock(
    visible: Boolean,
    readingPercentage: Int,
    isAutoScrolling: Boolean,
    autoScrollSpeed: Int,
    sysColors: SystemThemeColors,
    onToggleAutoScroll: () -> Unit,
    onAdjustAutoScrollSpeed: (Int) -> Unit,
    onOpenReaderSettings: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onToggleFocusMode: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 22.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(50.dp), spotColor = sysColors.accent.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(50.dp))
                    .border(1.dp, sysColors.border, RoundedCornerShape(50.dp))
                    .testTag("reader_floating_dock"),
                color = sysColors.surfaceGlass,
                shape = RoundedCornerShape(50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reading Percentage Indicator
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${persianNumber(readingPercentage)}٪",
                            color = sysColors.accent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            modifier = Modifier.testTag("reading_percentage_text")
                        )
                    }

                    // Auto-Scroll Controls Group
                    Surface(
                        color = sysColors.bg.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(50.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.border)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = onToggleAutoScroll,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("auto_scroll_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isAutoScrolling) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "پیمایش خودکار",
                                    tint = if (isAutoScrolling) sysColors.accent else sysColors.text,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { onAdjustAutoScrollSpeed(1) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("auto_scroll_speed_up")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "افزایش سرعت",
                                    tint = sysColors.textMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Text(
                                text = "${persianNumber(autoScrollSpeed)}x",
                                color = sysColors.accent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = { onAdjustAutoScrollSpeed(-1) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("auto_scroll_speed_down")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "کاهش سرعت",
                                    tint = sysColors.textMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Bookmarks Button
                    IconButton(
                        onClick = onOpenBookmarks,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_bookmarks_dock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "نشانک‌ها",
                            tint = sysColors.text,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Reader Settings Button
                    IconButton(
                        onClick = onOpenReaderSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_reader_settings_dock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "تنظیمات قلم و بوم",
                            tint = sysColors.text,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Focus Mode Button
                    IconButton(
                        onClick = onToggleFocusMode,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("toggle_focus_mode_dock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "حالت تمرکز",
                            tint = sysColors.text,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

fun persianNumber(number: Int): String {
    val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    return number.toString().map { char ->
        if (char.isDigit()) {
            persianDigits[char.toString().toInt()]
        } else {
            char.toString()
        }
    }.joinToString("")
}
