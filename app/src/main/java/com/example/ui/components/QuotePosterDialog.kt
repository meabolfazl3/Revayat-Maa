package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.NovelRepository
import com.example.data.model.PosterTemplate
import com.example.ui.theme.SystemThemeColors
import com.example.util.PosterImageGenerator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuotePosterDialog(
    quoteText: String,
    chapterTitle: String,
    sysColors: SystemThemeColors,
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var selectedTemplate by remember { mutableStateOf(PosterTemplate.TICKET) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 24.dp)
                    .testTag("quote_poster_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = sysColors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.border)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = sysColors.accent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "استودیوی ساخت پوستر و کارت نقل‌قول",
                                color = sysColors.text,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("close_quote_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = sysColors.textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Style Selector Chips
                    Text(
                        text = "سبک قاب پوستر",
                        color = sysColors.accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PosterTemplate.values().forEach { tpl ->
                            val isSelected = selectedTemplate == tpl
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedTemplate = tpl }
                                    .testTag("poster_template_${tpl.id}"),
                                color = if (isSelected) sysColors.primary else sysColors.surface.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) sysColors.accent else sysColors.border
                                )
                            ) {
                                Text(
                                    text = tpl.titleFa,
                                    color = if (isSelected) Color.White else sysColors.text,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Live Poster Card Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF06080D))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PosterCardRender(
                            template = selectedTemplate,
                            quoteText = quoteText,
                            chapterTitle = chapterTitle
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons - Share Poster Image Only
                    Button(
                        onClick = {
                            val success = PosterImageGenerator.generateAndSharePoster(
                                context = context,
                                template = selectedTemplate,
                                quoteText = quoteText,
                                chapterTitle = chapterTitle
                            )
                            if (success) {
                                onShowToast("تصویر پوستر آماده اشتراک‌گذاری شد 🖼️✨")
                            } else {
                                onShowToast("خطا در ایجاد تصویر پوستر")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("share_poster_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = sysColors.primary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اشتراک‌گذاری تصویر پوستر", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PosterCardRender(
    template: PosterTemplate,
    quoteText: String,
    chapterTitle: String
) {
    when (template) {
        PosterTemplate.TICKET -> TicketTemplateCard(quoteText, chapterTitle)
        PosterTemplate.CYBER_GLASS -> CyberGlassTemplateCard(quoteText, chapterTitle)
        PosterTemplate.IMPERIAL_GOLD -> ImperialGoldTemplateCard(quoteText, chapterTitle)
        PosterTemplate.DARK_EDITORIAL -> DarkEditorialTemplateCard(quoteText, chapterTitle)
    }
}

@Composable
private fun TicketTemplateCard(quoteText: String, chapterTitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF121820),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "برشی از رمان راز الماس",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = chapterTitle,
                    color = Color(0xFF555555),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dashed Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .drawBehind {
                        drawLine(
                            color = Color(0xFFCCCCCC),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quote Body
            Text(
                text = "«$quoteText»",
                color = Color(0xFF121820),
                fontSize = 16.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Justify,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Barcode & Author
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .drawBehind {
                        drawLine(
                            color = Color(0xFFCCCCCC),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mock Barcode Lines
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(24) { i ->
                    val isThick = (i % 3 == 0)
                    Box(
                        modifier = Modifier
                            .width(if (isThick) 3.dp else 1.5.dp)
                            .height(20.dp)
                            .background(Color(0xFF121820).copy(alpha = if (i % 5 == 0) 0.3f else 0.85f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "✍️ نویسنده: ${NovelRepository.NOVEL_AUTHOR}",
                    color = Color(0xFF222222),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "📢 کانال: ${NovelRepository.NOVEL_CHANNEL}",
                    color = Color(0xFF666666),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun CyberGlassTemplateCard(quoteText: String, chapterTitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF141E30),
                        Color(0xFF0A0F19)
                    )
                )
            )
            .border(1.5.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0x3300E5FF),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "💎 راز الماس",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = chapterTitle,
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "«$quoteText»",
                color = Color(0xFFF1F5F9),
                fontSize = 16.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Justify,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = Color(0x3300E5FF), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "✍️ اثر: ${NovelRepository.NOVEL_AUTHOR}",
                    color = Color(0xFFE0E7FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "📢 کانال: ${NovelRepository.NOVEL_CHANNEL}",
                    color = Color(0xFF7C92B3),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun ImperialGoldTemplateCard(quoteText: String, chapterTitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF5E6)),
        border = androidx.compose.foundation.BorderStroke(4.dp, Color(0xFF9E7D4A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📜 برشی از روایت کهن راز الماس",
                    color = Color(0xFF6E4018),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chapterTitle,
                    color = Color(0xFF965D2C),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFD4B483), thickness = 1.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "«$quoteText»",
                color = Color(0xFF2A1F14),
                fontSize = 17.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Justify,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFFD4B483), thickness = 1.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "✍️ به قلم: ${NovelRepository.NOVEL_AUTHOR}",
                    color = Color(0xFF5E4A36),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "📢 کانال: ${NovelRepository.NOVEL_CHANNEL}",
                    color = Color(0xFF8B6338),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun DarkEditorialTemplateCard(quoteText: String, chapterTitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0D1117))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5288C1))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "راز الماس",
                        color = Color(0xFFE6EDF3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = chapterTitle,
                    color = Color(0xFF5288C1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(80.dp)
                        .background(Color(0xFF2EA6FF), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "«$quoteText»",
                    color = Color(0xFFE6EDF3),
                    fontSize = 15.sp,
                    lineHeight = 27.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0x1AFFFFFF), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "✍️ نویسنده: ${NovelRepository.NOVEL_AUTHOR}",
                    color = Color(0xFF8B949E),
                    fontSize = 11.sp
                )
                Text(
                    text = "📢 کانال: ${NovelRepository.NOVEL_CHANNEL}",
                    color = Color(0xFF8B949E),
                    fontSize = 11.sp
                )
            }
        }
    }
}
