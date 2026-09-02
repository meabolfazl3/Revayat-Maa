package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.SystemThemeColors

@Composable
fun UnlockCodeDialog(
    sysColors: SystemThemeColors,
    isAlreadyUnlocked: Boolean,
    onSubmitCode: (String) -> Boolean,
    onResetLock: () -> Unit,
    onDismiss: () -> Unit
) {
    var inputCode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 24.dp)
                    .testTag("unlock_code_dialog"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = sysColors.surface),
                border = BorderStroke(1.dp, sysColors.border),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(22.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top Bar (Close icon & Title)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "قفل‌گشایی ویژه رمان",
                                color = sysColors.text,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_unlock_dialog_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = sysColors.text.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (isAlreadyUnlocked) {
                        // Already Unlocked Banner
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(44.dp)
                                )
                                Text(
                                    text = "دسترسی کامل فعال است",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "تمامی ۳۹ قسمت رمان «راز الماس» برای شما بازگشایی شده‌اند و می‌توانید بدون وقفه از خواندن رمان لذت ببرید.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = sysColors.text.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                onResetLock()
                                onDismiss()
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حذف کد دسترسی و قفل مجدد",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        // Enter Code State
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = sysColors.bg.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, sysColors.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "اگر کد دسترسی اختصاصی رمان «راز الماس» را از کانال یا نویسنده دریافت کرده‌اید، آن را در کادر زیر وارد کنید تا تمام قسمت‌ها فوراً باز شوند.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = sysColors.text.copy(alpha = 0.85f),
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(14.dp),
                                textAlign = TextAlign.Justify
                            )
                        }

                        OutlinedTextField(
                            value = inputCode,
                            onValueChange = {
                                inputCode = it
                                showError = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("unlock_code_input"),
                            label = { Text("کد دسترسی عددی") },
                            placeholder = { Text("کد ۸ رقمی") },
                            singleLine = true,
                            isError = showError,
                            supportingText = {
                                if (showError) {
                                    Text(
                                        text = "کد نامعتبر است! لطفاً مجدداً بررسی کنید.",
                                        color = Color(0xFFEF4444),
                                        fontSize = 11.sp
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    if (inputCode.isNotBlank()) {
                                        val success = onSubmitCode(inputCode)
                                        if (!success) {
                                            showError = true
                                        }
                                    } else {
                                        showError = true
                                    }
                                }
                            ),
                            trailingIcon = {
                                if (inputCode.isNotEmpty()) {
                                    IconButton(onClick = { inputCode = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "پاک کردن",
                                            tint = sysColors.text.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = sysColors.text,
                                unfocusedTextColor = sysColors.text,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = sysColors.border,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                if (inputCode.isBlank()) {
                                    showError = true
                                } else {
                                    val success = onSubmitCode(inputCode)
                                    if (!success) {
                                        showError = true
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_unlock_code_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "قفل‌گشایی و باز کردن تمام قسمت‌ها",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
