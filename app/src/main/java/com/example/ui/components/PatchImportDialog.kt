package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Chapter
import com.example.ui.theme.SystemThemeColors
import org.json.JSONObject

@Composable
fun PatchImportDialog(
    sysColors: SystemThemeColors,
    onImportChapter: (Chapter) -> Unit,
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 24.dp)
                    .testTag("patch_import_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = sysColors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.border)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SystemUpdateAlt,
                                contentDescription = null,
                                tint = sysColors.accent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "وارد کردن قسمت جدید رمان (Update Patch)",
                                color = sysColors.text,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("close_patch_dialog_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = sysColors.textMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "کد یا متن قسمت جدیدی که از کانال «روایت ما» کپی کرده‌اید را در کادر زیر قرار دهید تا قفل قسمت جدید در برنامه شما باز شود:",
                        color = sysColors.textMuted,
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = rawText,
                        onValueChange = {
                            rawText = it
                            isError = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .testTag("patch_input_field"),
                        placeholder = {
                            Text(
                                text = "کد JSON یا متن قسمت جدید را اینجا Paste کنید...\nمثال:\nقسمت ۱۳: شبح در جاده\nمتن پاراگراف اول...",
                                color = sysColors.textMuted.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = sysColors.bg.copy(alpha = 0.5f),
                            unfocusedContainerColor = sysColors.bg.copy(alpha = 0.5f),
                            focusedBorderColor = sysColors.accent,
                            unfocusedBorderColor = sysColors.border,
                            focusedTextColor = sysColors.text,
                            unfocusedTextColor = sysColors.text
                        )
                    )

                    if (isError) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "فرمت وارد شده صحیح نیست. لطفاً متن قسمت یا JSON را به درستی وارد کنید.",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            try {
                                val chapter = parseAndSanitizeEpisodePatch(rawText)
                                onImportChapter(chapter)
                                onShowToast("🎉 قسمت «${chapter.title}» با موفقیت بازگشایی شد!")
                                onDismiss()
                            } catch (e: Exception) {
                                isError = true
                                onShowToast(e.message ?: "خطا در پردازش متن پچ.")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("apply_patch_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = sysColors.primary)
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ثبت و بازگشایی قسمت جدید 🔓",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

fun parseAndSanitizeEpisodePatch(input: String): Chapter {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) throw IllegalArgumentException("متن ورودی خالی است.")

    // 1. Check if input is structured JSON payload
    if (trimmed.startsWith("{") && trimmed.contains("chapterId")) {
        val obj = JSONObject(trimmed)
        val chapterId = obj.getInt("chapterId")
        val rawTitle = obj.optString("title", "قسمت $chapterId")
        val cleanTitle = cleanTitleString(rawTitle, chapterId)
        val contentArr = obj.getJSONArray("content")
        val contentList = mutableListOf<String>()
        for (i in 0 until contentArr.length()) {
            val p = sanitizeParagraph(contentArr.getString(i))
            if (p.isNotBlank()) {
                contentList.add(p)
            }
        }
        if (contentList.isEmpty()) throw IllegalArgumentException("محتوای قسمت خالی است.")
        return Chapter(
            id = chapterId,
            title = cleanTitle,
            isLocked = false,
            content = contentList
        )
    }

    // 2. Text payload: intelligently detect chapter number, title, and body
    val rawLines = trimmed.lines().map { it.trim() }
    val nonEmptyLines = rawLines.filter { it.isNotBlank() }
    if (nonEmptyLines.isEmpty()) throw IllegalArgumentException("متن وارد شده نامعتبر است.")

    var chId = 13
    var cleanTitle = ""
    val bodyStartIndex: Int

    val firstLine = nonEmptyLines[0]
    val extractedId = extractChapterId(firstLine)

    if (firstLine.contains("قسمت") || firstLine.contains("فصل") || extractedId != null) {
        chId = extractedId ?: 13
        val titleInFirstLine = cleanTitleString(firstLine, chId)
        if (hasRealLetters(titleInFirstLine)) {
            // Title was on line 1 alongside chapter number (e.g. "قسمت ۱۳: سکوت دخمه")
            cleanTitle = titleInFirstLine
            bodyStartIndex = 1
        } else {
            // Line 1 was only chapter number/emoji (e.g. "قسمت سیزدهم 3️⃣1️⃣")
            // Title is in line 2
            if (nonEmptyLines.size > 1) {
                val secondLine = nonEmptyLines[1]
                val titleInSecondLine = cleanTitleString(secondLine, chId)
                if (hasRealLetters(titleInSecondLine)) {
                    cleanTitle = titleInSecondLine
                    bodyStartIndex = 2
                } else {
                    cleanTitle = "قسمت $chId"
                    bodyStartIndex = 1
                }
            } else {
                cleanTitle = "قسمت $chId"
                bodyStartIndex = 1
            }
        }
    } else {
        // First line is directly the title
        chId = extractChapterId(firstLine) ?: 13
        val t = cleanTitleString(firstLine, chId)
        cleanTitle = if (hasRealLetters(t)) t else "قسمت $chId"
        bodyStartIndex = 1
    }

    if (cleanTitle.isBlank()) {
        cleanTitle = "قسمت $chId"
    }

    // Collect body lines (excluding promo and channel ads)
    val bodyLines = mutableListOf<String>()
    for (i in bodyStartIndex until nonEmptyLines.size) {
        val line = nonEmptyLines[i]
        if (isPromoLine(line)) continue
        val cleanP = sanitizeParagraph(line)
        if (cleanP.isNotBlank()) {
            bodyLines.add(cleanP)
        }
    }

    if (bodyLines.isEmpty()) {
        if (nonEmptyLines.size > 1) {
            bodyLines.add(nonEmptyLines.last())
        } else {
            bodyLines.add(firstLine)
        }
    }

    return Chapter(
        id = chId,
        title = cleanTitle,
        isLocked = false,
        content = bodyLines
    )
}

private fun hasRealLetters(text: String): Boolean {
    return text.any { it.isLetter() }
}

private fun isPromoLine(line: String): Boolean {
    return line.contains("ما اینجاییم") ||
            line.contains("عضویت") ||
            (line.contains("کانال") && (line.contains("تلگرام") || line.contains("ایتا") || line.contains("روایت ما") || line.contains("http") || line.contains("@"))) ||
            line.contains("eitaa.com") ||
            line.contains("t.me") ||
            line.contains("ble.ir") ||
            line.contains("rubika.ir") ||
            line.startsWith("@") ||
            line.matches(Regex("""^[\s#_💎📖✨🌟🔹🔸🔻🔺💠•\-—=*~0-9۰-۹]+$""")) ||
            line.matches(Regex("""^#[\w_]+(\s+#[\w_]+)*\s*[📖💎✨]*$"""))
}

private fun extractChapterId(line: String): Int? {
    val persianDigits = mapOf('۰' to '0', '۱' to '1', '۲' to '2', '۳' to '3', '۴' to '4', '۵' to '5', '۶' to '6', '۷' to '7', '۸' to '8', '۹' to '9')
    val match = Regex("""(?:قسمت|فصل)\s*([۰-۹0-9]+)""").find(line)
    if (match != null) {
        val rawNum = match.groupValues[1].map { persianDigits[it] ?: it }.joinToString("")
        return rawNum.toIntOrNull()
    }
    // Word numbers
    if (line.contains("سیزدهم") || line.contains("۱۳")) return 13
    if (line.contains("چهاردهم") || line.contains("۱۴")) return 14
    if (line.contains("پانزدهم") || line.contains("۱۵")) return 15
    if (line.contains("شانزدهم") || line.contains("۱۶")) return 16
    if (line.contains("هفدهم") || line.contains("۱۷")) return 17
    if (line.contains("هجدهم") || line.contains("۱۸")) return 18
    if (line.contains("نوزدهم") || line.contains("۱۹")) return 19
    if (line.contains("بیستم") || line.contains("۲۰")) return 20
    if (line.contains("دوازدهم") || line.contains("۱۲")) return 12
    if (line.contains("یازدهم") || line.contains("۱۱")) return 11

    val generalMatch = Regex("""([۰-۹0-9]+)""").find(line)
    if (generalMatch != null) {
        val rawNum = generalMatch.groupValues[1].map { persianDigits[it] ?: it }.joinToString("")
        return rawNum.toIntOrNull()
    }
    return null
}

private fun cleanTitleString(raw: String, chId: Int): String {
    // 1. Remove hashtags, channel links, and author/channel signatures
    var clean = raw
        .replace(Regex("""#[\w_]+"""), "")
        .replace(Regex("""https?://[^\s]+"""), "")
        .replace(Regex("""(?:www\.)?(?:eitaa|t\.me|ble|rubika)\.[a-z]+/[^\s]+"""), "")
        .replace(Regex("""@[\w_]+"""), "")
        .replace(Regex("""نویسنده\s*:\s*[^|\n]+"""), "")
        .replace(Regex("""کانال\s*:\s*[^|\n]+"""), "")

    // 2. Remove "قسمت / فصل [عدد یا کلمه]"
    clean = clean.replace(
        Regex("""(?i)^(?:[📖💎✨🌟🔹🔸🔻🔺💠•\-—=*~\s]*)(?:قسمت|فصل)\s*(?:[۰-۹0-9]+|اول|دوم|سوم|چهارم|پنجم|ششم|هفتم|هشتم|نهم|دهم|یازدهم|دوازدهم|سیزدهم|چهاردهم|پانزدهم|شانزدهم|هفدهم|هجدهم|نوزدهم|بیستم|بیست\s*و\s*(?:یکم|دوم|سوم|چهارم|پنجم|ششم|هفتم|هشتم|نهم)|سی‌ام|سی\s*و\s*(?:یکم|دوم|سوم|چهارم|پنجم|ششم|هفتم|هشتم|نهم))\s*"""),
        ""
    )

    // 3. Remove all emojis, keycap digits (3️⃣1️⃣ etc.), and symbols
    val emojiAndSymbolPattern = Regex("""[\p{So}\p{Sk}\p{Sm}\p{Sc}\p{Cn}\p{Cs}\uD800-\uDFFF\u2600-\u27BF\uFE00-\uFE0F\u1F300-\u1F9FF\u20E3]+""")
    clean = clean.replace(emojiAndSymbolPattern, " ")

    // 4. Strip leading/trailing delimiters (colons, dashes, pipes, quotes, bullets, spaces)
    clean = clean.replace(Regex("""^[ \t:\-—–_•|«»""''*~]+"""), "")
        .replace(Regex("""[ \t:\-—–_•|«»""''*~]+$"""), "")
        .trim()

    // 5. If what remains is just numbers or whitespace, clear it
    if (clean.matches(Regex("""^[0-9۰-۹\s]*$"""))) {
        clean = ""
    }

    return clean
}

private fun sanitizeParagraph(p: String): String {
    return p
        .replace(Regex("""#رمان\s*"""), "")
        .replace(Regex("""#راز_الماس\s*"""), "")
        .replace(Regex("""#رازالماس\s*"""), "")
        .replace(Regex("""#روایت_ما\s*"""), "")
        .replace(Regex("""#[\w_]+"""), "")
        .replace(Regex("""https?://[^\s]+"""), "")
        .replace(Regex("""(?:www\.)?eitaa\.com/[^\s]+"""), "")
        .replace(Regex("""(?:www\.)?t\.me/[^\s]+"""), "")
        .replace(Regex("""@revayatema"""), "")
        .replace(Regex("""ما اینجاییم\s*👇?"""), "")
        .trim()
}

