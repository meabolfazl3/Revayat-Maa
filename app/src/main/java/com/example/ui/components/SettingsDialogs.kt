package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.data.NovelRepository
import com.example.data.model.PersianFont
import com.example.data.model.ReaderCanvasTheme
import com.example.data.model.ReaderUiSettings
import com.example.data.model.ReadingMode
import com.example.data.model.SystemTheme
import com.example.ui.theme.NovelThemes
import com.example.ui.theme.SystemThemeColors

/**
 * Telegram-Style Reader & Appearance Settings Dialog with Live Novel Preview Box
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReaderSettingsDialog(
    currentSettings: ReaderUiSettings,
    sysColors: SystemThemeColors,
    onSettingsChanged: (ReaderUiSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val readerColors = NovelThemes.getReaderColors(currentSettings.readerTheme)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 20.dp)
                    .testTag("reader_settings_dialog"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = sysColors.surface),
                border = BorderStroke(1.dp, sysColors.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Dialog Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = sysColors.accent.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = sysColors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "تنظیمات ظاهر و مطالعه",
                                    color = sysColors.text,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "شخصی‌سازی زنده و بلادرنگ مطالعه",
                                    color = sysColors.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = sysColors.bg.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, sysColors.border),
                            modifier = Modifier.size(34.dp)
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("close_reader_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "بستن",
                                    tint = sysColors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // 2. LIVE NOVEL PREVIEW BOX (باکس پیش‌نمایش زنده متن کتاب)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = readerColors.bg,
                        border = BorderStroke(1.5.dp, readerColors.title.copy(alpha = 0.35f)),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("live_novel_preview_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Preview Box Header Tag
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = readerColors.badgeBg,
                                    border = BorderStroke(1.dp, readerColors.badgeText.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(readerColors.badgeText)
                                        )
                                        Text(
                                            text = "پیش‌نمایش زنده متن رمان",
                                            color = readerColors.badgeText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "قسمت ۷ — تالار کتیبه‌ها",
                                    color = readerColors.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Dynamic Live Text that instantly updates
                            Text(
                                text = "آریا دستش را روی کتیبه باستانی گذاشت و با صدایی آرام گفت: تا وقتی کنار هم باشیم هیچ رمزی بسته نمی‌مونه... راز الماس در قلب همین تاریکی پنهان شده است.",
                                color = readerColors.text,
                                fontFamily = NovelThemes.getFontFamily(currentSettings.readerFont),
                                fontSize = currentSettings.fontSizeSp.sp,
                                lineHeight = (currentSettings.fontSizeSp * currentSettings.lineHeightMultiplier).sp,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("live_preview_text")
                            )
                        }
                    }

                    // 3. READING MODE SELECTOR (⚙️ حالت مطالعه / نوع نمایش)
                    ReadingModeSegmentedControl(
                        selectedMode = currentSettings.readingMode,
                        sysColors = sysColors,
                        onModeSelected = { mode ->
                            onSettingsChanged(currentSettings.copy(readingMode = mode))
                        },
                        tagPrefix = "reader_dialog"
                    )

                    // 4. TEXT SIZE SLIDER (اسلایدر اندازه متن با نمایش عدد زنده تلگرامی)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(sysColors.surfaceGlass)
                            .border(BorderStroke(1.dp, sysColors.border), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = null,
                                    tint = sysColors.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "اندازه قلم متن",
                                    color = sysColors.text,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Live Number Badge (Sleek Capsule)
                            SleekValueBadge(
                                text = "${currentSettings.fontSizeSp.toInt()} pt",
                                accentColor = sysColors.accent
                            )
                        }

                        // Telegram/iOS Style Minimal Slider with A...A icons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "A",
                                color = sysColors.textMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            MinimalSleekSlider(
                                value = currentSettings.fontSizeSp,
                                onValueChange = {
                                    onSettingsChanged(currentSettings.copy(fontSizeSp = it))
                                },
                                valueRange = 14f..34f,
                                steps = 0,
                                accentColor = sysColors.accent,
                                trackBgColor = sysColors.border.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f),
                                testTag = "reader_font_size_slider"
                            )

                            Text(
                                text = "A",
                                color = sysColors.text,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // 4. HORIZONTAL THEME CAROUSEL (انتخابگر افقی پوسته‌های رنگی مشابه تلگرام)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "پوسته رنگی بوم مطالعه",
                            color = sysColors.text,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ReaderCanvasTheme.values().forEach { themeOption ->
                                val isSelected = currentSettings.readerTheme == themeOption
                                val optionColors = NovelThemes.getReaderColors(themeOption)

                                val scale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.03f else 1.0f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "themeScale"
                                )

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = optionColors.bg,
                                    border = BorderStroke(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) sysColors.accent else optionColors.title.copy(alpha = 0.25f)
                                    ),
                                    shadowElevation = if (isSelected) 6.dp else 2.dp,
                                    modifier = Modifier
                                        .width(78.dp)
                                        .height(106.dp)
                                        .scale(scale)
                                        .clickable {
                                            onSettingsChanged(currentSettings.copy(readerTheme = themeOption))
                                        }
                                        .testTag("reader_theme_option_${themeOption.id}")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp)
                                    ) {
                                        // Selected Check Badge
                                        if (isSelected) {
                                            Surface(
                                                shape = CircleShape,
                                                color = sysColors.accent,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .align(Alignment.TopEnd)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            // Mock Text Lines / Preview in the card
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = if (isSelected) 14.dp else 4.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "Aa",
                                                    color = optionColors.text,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.7f)
                                                        .height(3.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(optionColors.title.copy(alpha = 0.5f))
                                                )
                                            }

                                            // Theme Title Pill
                                            Surface(
                                                shape = RoundedCornerShape(50.dp),
                                                color = optionColors.surface.copy(alpha = 0.8f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = themeOption.titleFa,
                                                    color = optionColors.text,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    modifier = Modifier.padding(vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5. PERSIAN FONT SELECTION (انتخاب فونت ادبی)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = null,
                                tint = sysColors.accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "قلم ادبی رمان",
                                color = sysColors.text,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PersianFont.values().forEach { fontOption ->
                                val isSelected = currentSettings.readerFont == fontOption
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) sysColors.accent else sysColors.surfaceGlass,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) sysColors.accent else sysColors.border
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            onSettingsChanged(currentSettings.copy(readerFont = fontOption))
                                        }
                                        .testTag("reader_font_${fontOption.name}")
                                ) {
                                    Text(
                                        text = fontOption.titleFa,
                                        color = if (isSelected) Color.White else sysColors.text,
                                        fontSize = 13.sp,
                                        fontFamily = NovelThemes.getFontFamily(fontOption),
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 6. LINE SPACING SELECTOR (فاصله خطوط)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(sysColors.surfaceGlass)
                            .border(BorderStroke(1.dp, sysColors.border), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatLineSpacing,
                                    contentDescription = null,
                                    tint = sysColors.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "فاصله بین خطوط",
                                    color = sysColors.text,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            SleekValueBadge(
                                text = String.format(java.util.Locale.US, "%.1fx", currentSettings.lineHeightMultiplier),
                                accentColor = sysColors.accent
                            )
                        }

                        // 3 Quick Presets (فشرده، استاندارد، باز)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "فشرده" to 1.8f,
                                "استاندارد" to 2.2f,
                                "خوانا و باز" to 2.6f
                            ).forEach { (label, value) ->
                                val isSelected = kotlin.math.abs(currentSettings.lineHeightMultiplier - value) < 0.15f
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) sysColors.primary else sysColors.bg.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) sysColors.accent else sysColors.border
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onSettingsChanged(currentSettings.copy(lineHeightMultiplier = value))
                                        }
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else sysColors.textMuted,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        // Fine-tuning Minimal Slider
                        MinimalSleekSlider(
                            value = currentSettings.lineHeightMultiplier,
                            onValueChange = {
                                onSettingsChanged(currentSettings.copy(lineHeightMultiplier = it))
                            },
                            valueRange = 1.6f..3.0f,
                            steps = 0,
                            accentColor = sysColors.accent,
                            trackBgColor = sysColors.border.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "reader_line_height_slider"
                        )
                    }
                }
            }
        }
    }
}

/**
 * System Settings Dialog (App-wide UI Theme, Scaling & Font)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SystemSettingsDialog(
    currentSettings: ReaderUiSettings,
    sysColors: SystemThemeColors,
    onSettingsChanged: (ReaderUiSettings) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 24.dp)
                    .testTag("system_settings_dialog"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = sysColors.surface),
                border = BorderStroke(1.dp, sysColors.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = sysColors.accent.copy(alpha = 0.15f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = sysColors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "مرکز کنترل برنامه",
                                    color = sysColors.text,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "شخصی‌سازی ظاهر سیستم",
                                    color = sysColors.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = sysColors.bg.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, sysColors.border),
                            modifier = Modifier.size(34.dp)
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("close_system_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "بستن",
                                    tint = sysColors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Novel Info Banner
                    Surface(
                        color = sysColors.surfaceGlass,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, sysColors.border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppLogo(
                                modifier = Modifier.size(48.dp),
                                shapeRadius = 14.dp,
                                elevation = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = NovelRepository.APP_NAME,
                                    color = sysColors.text,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "تولید کننده : ${NovelRepository.PRODUCER_NAME} | کانال: ${NovelRepository.NOVEL_CHANNEL}",
                                    color = sysColors.textMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // System Theme Horizontal Row (Telegram Style)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "پوسته نرم‌افزار و رابط کاربری",
                            color = sysColors.text,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SystemTheme.values().forEach { themeItem ->
                                val isSelected = currentSettings.systemTheme == themeItem
                                val colors = NovelThemes.getSystemColors(themeItem)

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = colors.bg,
                                    border = BorderStroke(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) sysColors.accent else colors.border
                                    ),
                                    shadowElevation = if (isSelected) 6.dp else 2.dp,
                                    modifier = Modifier
                                        .width(76.dp)
                                        .height(96.dp)
                                        .clickable {
                                            onSettingsChanged(currentSettings.copy(systemTheme = themeItem))
                                        }
                                        .testTag("sys_theme_option_${themeItem.id}")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    ) {
                                        if (isSelected) {
                                            Surface(
                                                shape = CircleShape,
                                                color = sysColors.accent,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .align(Alignment.TopEnd)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .padding(top = 4.dp)
                                                    .clip(CircleShape)
                                                    .background(colors.primary)
                                            )

                                            Surface(
                                                shape = RoundedCornerShape(50.dp),
                                                color = colors.surface.copy(alpha = 0.9f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = themeItem.titleFa,
                                                    color = colors.text,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    modifier = Modifier.padding(vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Global UI Font Selector
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "قلم عمومی منوها و سیستم",
                            color = sysColors.text,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PersianFont.values().forEach { fontItem ->
                                val isSelected = currentSettings.uiFont == fontItem
                                Surface(
                                    modifier = Modifier
                                        .clickable {
                                            onSettingsChanged(currentSettings.copy(uiFont = fontItem))
                                        }
                                        .testTag("ui_font_${fontItem.name}"),
                                    color = if (isSelected) sysColors.primary else sysColors.surfaceGlass,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) sysColors.accent else sysColors.border
                                    )
                                ) {
                                    Text(
                                        text = fontItem.titleFa,
                                        color = if (isSelected) Color.White else sysColors.text,
                                        fontSize = 13.sp,
                                        fontFamily = NovelThemes.getFontFamily(fontItem),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Reading Mode Selector in Main System Settings
                    ReadingModeSegmentedControl(
                        selectedMode = currentSettings.readingMode,
                        sysColors = sysColors,
                        onModeSelected = { mode ->
                            onSettingsChanged(currentSettings.copy(readingMode = mode))
                        },
                        tagPrefix = "system_dialog"
                    )

                    // UI Scaling Slider
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(sysColors.surfaceGlass)
                            .border(BorderStroke(1.dp, sysColors.border), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "اندازه المان‌های پنل (UI Scaling)",
                                color = sysColors.text,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            SleekValueBadge(
                                text = "${currentSettings.uiScalePercent}٪",
                                accentColor = sysColors.accent
                            )
                        }

                        MinimalSleekSlider(
                            value = currentSettings.uiScalePercent.toFloat(),
                            onValueChange = {
                                onSettingsChanged(currentSettings.copy(uiScalePercent = it.toInt()))
                            },
                            valueRange = 85f..125f,
                            steps = 0,
                            accentColor = sysColors.primary,
                            trackBgColor = sysColors.border.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "ui_scale_slider"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Glassmorphic Telegram-style Segmented Switcher for Reading Mode
 */
@Composable
fun ReadingModeSegmentedControl(
    selectedMode: ReadingMode,
    sysColors: SystemThemeColors,
    onModeSelected: (ReadingMode) -> Unit,
    tagPrefix: String = "reader",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(sysColors.surfaceGlass)
            .border(BorderStroke(1.dp, sysColors.border), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "⚙️",
                    fontSize = 14.sp
                )
                Text(
                    text = "حالت مطالعه / نوع نمایش",
                    color = sysColors.text,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = sysColors.accent.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, sysColors.accent.copy(alpha = 0.3f))
            ) {
                Text(
                    text = if (selectedMode == ReadingMode.SCROLL) "اسکرول پیوسته" else "ورق‌زدن اسلایدی",
                    color = sysColors.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }

        // Glassmorphic Segmented Control Frame
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = sysColors.bg.copy(alpha = 0.65f),
            border = BorderStroke(1.dp, sysColors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(ReadingMode.SCROLL, ReadingMode.PAGE_FLIP).forEach { mode ->
                    val isSelected = selectedMode == mode
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.0f else 0.97f,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                        label = "mode_scale_$tagPrefix"
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) sysColors.primary else Color.Transparent,
                        border = if (isSelected) BorderStroke(1.dp, sysColors.accent) else null,
                        shadowElevation = if (isSelected) 4.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onModeSelected(mode)
                            }
                            .testTag("${tagPrefix}_reading_mode_${mode.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = mode.emoji,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = mode.titleFa,
                                    color = if (isSelected) Color.White else sysColors.text,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Text(
                                    text = mode.descFa,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else sysColors.textMuted,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Minimalist Telegram / iOS style sleek slider component
 * 4px delicate track, rounded pill corners, 18px smooth circular thumb with soft elevation & spring animation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalSleekSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    accentColor: Color,
    trackBgColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val thumbScale by animateFloatAsState(
        targetValue = if (isPressed) 1.22f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "thumb_scale"
    )

    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        interactionSource = interactionSource,
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            activeTrackColor = accentColor,
            inactiveTrackColor = trackBgColor,
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        ),
        thumb = {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .scale(thumbScale)
                    .shadow(3.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.4f))
                    .background(Color.White, CircleShape)
                    .border(2.5.dp, accentColor, CircleShape)
            )
        },
        track = { sliderPositions ->
            val fraction = if (sliderPositions.valueRange.endInclusive > sliderPositions.valueRange.start) {
                ((sliderPositions.value - sliderPositions.valueRange.start) / (sliderPositions.valueRange.endInclusive - sliderPositions.valueRange.start)).coerceIn(0f, 1f)
            } else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(trackBgColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor)
                )
            }
        },
        modifier = modifier.testTag(testTag)
    )
}

/**
 * Modern iOS / Telegram Glassmorphic Capsule Value Badge
 */
@Composable
fun SleekValueBadge(
    text: String,
    accentColor: Color,
    bgColor: Color = accentColor.copy(alpha = 0.12f),
    borderColor: Color = accentColor.copy(alpha = 0.28f),
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = accentColor,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

