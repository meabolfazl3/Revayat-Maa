package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PersianFont
import com.example.data.model.ReaderCanvasTheme
import com.example.data.model.SystemTheme

// Palette Definitions
data class SystemThemeColors(
    val bg: Color,
    val surface: Color,
    val surfaceGlass: Color,
    val border: Color,
    val text: Color,
    val textMuted: Color,
    val primary: Color,
    val accent: Color,
    val isDark: Boolean
)

data class ReaderCanvasColors(
    val bg: Color,
    val surface: Color,
    val title: Color,
    val text: Color,
    val textSecondary: Color,
    val badgeBg: Color,
    val badgeText: Color,
    val isDark: Boolean
)

object NovelThemes {

    fun getSystemColors(theme: SystemTheme): SystemThemeColors {
        return when (theme) {
            SystemTheme.TELEGRAM_DARK -> SystemThemeColors(
                bg = Color(0xFF0E1621),
                surface = Color(0xFF17212B),
                surfaceGlass = Color(0xE617212B),
                border = Color(0x2EFFFFFF),
                text = Color(0xFFF5F5F5),
                textMuted = Color(0xFF9AB0C4),
                primary = Color(0xFF5288C1),
                accent = Color(0xFF2EA6FF),
                isDark = true
            )
            SystemTheme.AMOLED_BLACK -> SystemThemeColors(
                bg = Color(0xFF000000),
                surface = Color(0xFF0C0C0E),
                surfaceGlass = Color(0xE60C0C0E),
                border = Color(0x38FFFFFF),
                text = Color(0xFFFFFFFF),
                textMuted = Color(0xFFA0A0A0),
                primary = Color(0xFF3390EC),
                accent = Color(0xFF60A5FA),
                isDark = true
            )
            SystemTheme.TELEGRAM_DAY -> SystemThemeColors(
                bg = Color(0xFFF0F3F7),
                surface = Color(0xFFFFFFFF),
                surfaceGlass = Color(0xF0FFFFFF),
                border = Color(0x1F000000),
                text = Color(0xFF0F1821),
                textMuted = Color(0xFF526372),
                primary = Color(0xFF1F73B7),
                accent = Color(0xFF0284C7),
                isDark = false
            )
            SystemTheme.CYBER_NEON -> SystemThemeColors(
                bg = Color(0xFF080A12),
                surface = Color(0xFF101426),
                surfaceGlass = Color(0xE6101426),
                border = Color(0x5500E5FF),
                text = Color(0xFFE2F3F5),
                textMuted = Color(0xFF90A8CC),
                primary = Color(0xFF00E5FF),
                accent = Color(0xFFFF007F),
                isDark = true
            )
            SystemTheme.SEPIA_PAPER -> SystemThemeColors(
                bg = Color(0xFFF5EFEB),
                surface = Color(0xFFEAE2DA),
                surfaceGlass = Color(0xF0EAE2DA),
                border = Color(0x338D6E63),
                text = Color(0xFF2B1D0C),
                textMuted = Color(0xFF7D6556),
                primary = Color(0xFF8D6E63),
                accent = Color(0xFF8D6E63),
                isDark = false
            )
            SystemTheme.PURE_LIGHT -> SystemThemeColors(
                bg = Color(0xFFFAFAFA),
                surface = Color(0xFFFFFFFF),
                surfaceGlass = Color(0xF0FFFFFF),
                border = Color(0x1F1E293B),
                text = Color(0xFF1E293B),
                textMuted = Color(0xFF64748B),
                primary = Color(0xFF2563EB),
                accent = Color(0xFF2563EB),
                isDark = false
            )
            SystemTheme.SOOTHING_MINT -> SystemThemeColors(
                bg = Color(0xFFEBF3EC),
                surface = Color(0xFFDEEADC),
                surfaceGlass = Color(0xF0DEEADC),
                border = Color(0x3310B981),
                text = Color(0xFF153E26),
                textMuted = Color(0xFF2D6A4F),
                primary = Color(0xFF10B981),
                accent = Color(0xFF10B981),
                isDark = false
            )
            SystemTheme.WARM_SAND -> SystemThemeColors(
                bg = Color(0xFFFBF6EE),
                surface = Color(0xFFF0E5D5),
                surfaceGlass = Color(0xF0F0E5D5),
                border = Color(0x33D97706),
                text = Color(0xFF372A1F),
                textMuted = Color(0xFF785A3C),
                primary = Color(0xFFD97706),
                accent = Color(0xFFD97706),
                isDark = false
            )
            SystemTheme.CYBER_PURPLE_NEON -> SystemThemeColors(
                bg = Color(0xFF140A28),
                surface = Color(0xFF1F103C),
                surfaceGlass = Color(0xE61F103C),
                border = Color(0x55D946EF),
                text = Color(0xFFFAF5FF),
                textMuted = Color(0xFFE879F9),
                primary = Color(0xFFD946EF),
                accent = Color(0xFFE879F9),
                isDark = true
            )
            SystemTheme.IMPERIAL_JADE -> SystemThemeColors(
                bg = Color(0xFF0A1612),
                surface = Color(0xFF11251E),
                surfaceGlass = Color(0xE611251E),
                border = Color(0x5510B981),
                text = Color(0xFFECFDF5),
                textMuted = Color(0xFF6EE7B7),
                primary = Color(0xFF10B981),
                accent = Color(0xFFF59E0B),
                isDark = true
            )
            SystemTheme.NORDIC_AURORA -> SystemThemeColors(
                bg = Color(0xFF091E24),
                surface = Color(0xFF102E38),
                surfaceGlass = Color(0xE6102E38),
                border = Color(0x5506B6D4),
                text = Color(0xFFECFEFF),
                textMuted = Color(0xFF67E8F9),
                primary = Color(0xFF06B6D4),
                accent = Color(0xFF38BDF8),
                isDark = true
            )
            SystemTheme.ROYAL_CRIMSON -> SystemThemeColors(
                bg = Color(0xFF1A0A0F),
                surface = Color(0xFF2B1018),
                surfaceGlass = Color(0xE62B1018),
                border = Color(0x55FB7185),
                text = Color(0xFFFFF1F2),
                textMuted = Color(0xFFFDA4AF),
                primary = Color(0xFFFB7185),
                accent = Color(0xFFE11D48),
                isDark = true
            )
            SystemTheme.NORDIC_CLEAN -> SystemThemeColors(
                bg = Color(0xFFEEF2F7),
                surface = Color(0xFFFFFFFF),
                surfaceGlass = Color(0xF0FFFFFF),
                border = Color(0x1F1E293B),
                text = Color(0xFF0F172A),
                textMuted = Color(0xFF475569),
                primary = Color(0xFF0284C7),
                accent = Color(0xFF0284C7),
                isDark = false
            )
        }
    }

    fun getReaderColors(theme: ReaderCanvasTheme): ReaderCanvasColors {
        return when (theme) {
            ReaderCanvasTheme.SEPIA_PAPER -> ReaderCanvasColors(
                bg = Color(0xFFF5EFEB),
                surface = Color(0xFFEAE2DA),
                title = Color(0xFF2B1D0C),
                text = Color(0xFF2B1D0C),
                textSecondary = Color(0xFF7D6556),
                badgeBg = Color(0x2B8D6E63),
                badgeText = Color(0xFF8D6E63),
                isDark = false
            )
            ReaderCanvasTheme.PURE_LIGHT -> ReaderCanvasColors(
                bg = Color(0xFFFAFAFA),
                surface = Color(0xFFFFFFFF),
                title = Color(0xFF1E293B),
                text = Color(0xFF1E293B),
                textSecondary = Color(0xFF64748B),
                badgeBg = Color(0x1F2563EB),
                badgeText = Color(0xFF2563EB),
                isDark = false
            )
            ReaderCanvasTheme.SOOTHING_MINT, ReaderCanvasTheme.MINT_FRESH -> ReaderCanvasColors(
                bg = Color(0xFFEBF3EC),
                surface = Color(0xFFDEEADC),
                title = Color(0xFF153E26),
                text = Color(0xFF153E26),
                textSecondary = Color(0xFF2D6A4F),
                badgeBg = Color(0x2610B981),
                badgeText = Color(0xFF10B981),
                isDark = false
            )
            ReaderCanvasTheme.WARM_SAND -> ReaderCanvasColors(
                bg = Color(0xFFFBF6EE),
                surface = Color(0xFFF0E5D5),
                title = Color(0xFF372A1F),
                text = Color(0xFF372A1F),
                textSecondary = Color(0xFF6C5340),
                badgeBg = Color(0x26D97706),
                badgeText = Color(0xFFD97706),
                isDark = false
            )
            ReaderCanvasTheme.CHARCOAL -> ReaderCanvasColors(
                bg = Color(0xFF121922),
                surface = Color(0xFF1A232E),
                title = Color(0xFF5EA4E8),
                text = Color(0xFFE1E6EB),
                textSecondary = Color(0xFF94A3B8),
                badgeBg = Color(0x265EA4E8),
                badgeText = Color(0xFF70B4F7),
                isDark = true
            )
            ReaderCanvasTheme.AMOLED -> ReaderCanvasColors(
                bg = Color(0xFF000000),
                surface = Color(0xFF0A0A0A),
                title = Color(0xFF60A5FA),
                text = Color(0xFFF1F5F9),
                textSecondary = Color(0xFFA3A3A3),
                badgeBg = Color(0x1FFFFFFF),
                badgeText = Color(0xFF60A5FA),
                isDark = true
            )
            ReaderCanvasTheme.MIDNIGHT_SLATE -> ReaderCanvasColors(
                bg = Color(0xFF0B101B),
                surface = Color(0xFF141B2B),
                title = Color(0xFF38BDF8),
                text = Color(0xFFE0E7FF),
                textSecondary = Color(0xFF818CF8),
                badgeBg = Color(0x2638BDF8),
                badgeText = Color(0xFF38BDF8),
                isDark = true
            )
            ReaderCanvasTheme.PARCHMENT -> ReaderCanvasColors(
                bg = Color(0xFFF5EEDB),
                surface = Color(0xFFE9DEC3),
                title = Color(0xFF6E4018),
                text = Color(0xFF241B11),
                textSecondary = Color(0xFF5E4A36),
                badgeBg = Color(0x1F6E4018),
                badgeText = Color(0xFF6E4018),
                isDark = false
            )
            ReaderCanvasTheme.SOFT_MILK -> ReaderCanvasColors(
                bg = Color(0xFFF8FAFC),
                surface = Color(0xFFFFFFFF),
                title = Color(0xFF0F172A),
                text = Color(0xFF1E293B),
                textSecondary = Color(0xFF475569),
                badgeBg = Color(0x1A0284C7),
                badgeText = Color(0xFF0284C7),
                isDark = false
            )
        }
    }

    fun toM3ColorScheme(sysColors: SystemThemeColors): ColorScheme {
        return if (sysColors.isDark) {
            darkColorScheme(
                primary = sysColors.primary,
                secondary = sysColors.accent,
                tertiary = sysColors.primary,
                background = sysColors.bg,
                surface = sysColors.surface,
                surfaceVariant = sysColors.surfaceGlass,
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = sysColors.text,
                onSurface = sysColors.text,
                onSurfaceVariant = sysColors.textMuted,
                outline = sysColors.border
            )
        } else {
            lightColorScheme(
                primary = sysColors.primary,
                secondary = sysColors.accent,
                tertiary = sysColors.primary,
                background = sysColors.bg,
                surface = sysColors.surface,
                surfaceVariant = sysColors.surfaceGlass,
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = sysColors.text,
                onSurface = sysColors.text,
                onSurfaceVariant = sysColors.textMuted,
                outline = sysColors.border
            )
        }
    }

    val VazirmatnFontFamily = FontFamily(
        Font(R.font.font_vazirmatn_regular, FontWeight.Normal),
        Font(R.font.font_vazirmatn_medium, FontWeight.Medium),
        Font(R.font.font_vazirmatn_bold, FontWeight.Bold),
        Font(R.font.font_vazirmatn_bold, FontWeight.ExtraBold)
    )

    val SahelFontFamily = FontFamily(
        Font(R.font.font_sahel_regular, FontWeight.Normal),
        Font(R.font.font_sahel_bold, FontWeight.Bold)
    )

    val ShabnamFontFamily = FontFamily(
        Font(R.font.font_shabnam_regular, FontWeight.Normal),
        Font(R.font.font_shabnam_bold, FontWeight.Bold)
    )

    val MarkaziFontFamily = FontFamily(
        Font(R.font.font_markazi_regular, FontWeight.Normal),
        Font(R.font.font_markazi_regular, FontWeight.Bold)
    )

    val AmiriFontFamily = FontFamily(
        Font(R.font.font_amiri_regular, FontWeight.Normal),
        Font(R.font.font_amiri_bold, FontWeight.Bold)
    )

    val LateefFontFamily = FontFamily(
        Font(R.font.font_lateef_regular, FontWeight.Normal),
        Font(R.font.font_lateef_bold, FontWeight.Bold)
    )

    val LalezarFontFamily = FontFamily(
        Font(R.font.font_lalezar_regular, FontWeight.Normal),
        Font(R.font.font_lalezar_regular, FontWeight.Bold)
    )

    fun getFontFamily(persianFont: PersianFont): FontFamily {
        return when (persianFont) {
            PersianFont.VAZIRMATN -> VazirmatnFontFamily
            PersianFont.SAHEL -> SahelFontFamily
            PersianFont.SHABNAM -> ShabnamFontFamily
            PersianFont.MARKAZI -> MarkaziFontFamily
            PersianFont.AMIRI -> AmiriFontFamily
            PersianFont.LATEEF -> LateefFontFamily
            PersianFont.LALEZAR -> LalezarFontFamily
        }
    }

    fun createTypography(persianFont: PersianFont): androidx.compose.material3.Typography {
        val family = getFontFamily(persianFont)
        return androidx.compose.material3.Typography(
            displayLarge = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            displayMedium = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            headlineLarge = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            headlineMedium = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            titleLarge = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            titleMedium = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            ),
            titleSmall = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            bodyLarge = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 26.sp
            ),
            bodyMedium = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 22.sp
            ),
            bodySmall = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            ),
            labelLarge = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            labelMedium = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            labelSmall = androidx.compose.ui.text.TextStyle(
                fontFamily = family,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )
        )
    }
}
