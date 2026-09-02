package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.data.model.PersianFont
import com.example.data.model.SystemTheme
import com.example.ui.theme.NovelThemes

@Composable
fun RazeAlmasTheme(
    systemTheme: SystemTheme = SystemTheme.TELEGRAM_DARK,
    uiFont: PersianFont = PersianFont.VAZIRMATN,
    uiScalePercent: Int = 100,
    content: @Composable () -> Unit
) {
    val sysColors = NovelThemes.getSystemColors(systemTheme)
    val colorScheme = NovelThemes.toM3ColorScheme(sysColors)
    val typography = NovelThemes.createTypography(uiFont)

    val currentDensity = LocalDensity.current
    val scaleFactor = (uiScalePercent / 100f).coerceIn(0.75f, 1.35f)
    val scaledDensity = Density(
        density = currentDensity.density * scaleFactor,
        fontScale = currentDensity.fontScale * scaleFactor
    )

    CompositionLocalProvider(LocalDensity provides scaledDensity) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}

