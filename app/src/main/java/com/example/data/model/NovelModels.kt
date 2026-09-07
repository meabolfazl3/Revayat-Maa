package com.example.data.model

data class Chapter(
    val id: Int,
    val title: String,
    val isLocked: Boolean = false,
    val content: List<String> = emptyList()
)

data class Bookmark(
    val id: String = java.util.UUID.randomUUID().toString(),
    val chapterId: Int,
    val chapterTitle: String,
    val paragraphIndex: Int,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SystemTheme(
    val id: String,
    val titleFa: String,
    val requiredBadges: Int = 0,
    val isDark: Boolean = true
) {
    TELEGRAM_DARK("telegram-dark", "تاریک مدرن", 0, true),
    AMOLED_BLACK("amoled-black", "اولد مشکی", 0, true),
    TELEGRAM_DAY("telegram-day", "روشن روز", 0, false),
    CYBER_NEON("cyber-neon", "سایبر نئون", 0, true),
    SEPIA_PAPER("sepia-paper", "کاغذ کاهی کلاسیک", 0, false),
    PURE_LIGHT("pure-light", "صبح مینیمال", 0, false),
    CYBER_PURPLE_NEON("cyber-purple-neon", "نئون بنفش", 2, true),
    IMPERIAL_JADE("imperial-jade", "یشمی کهکشانی", 5, true),
    NORDIC_AURORA("nordic-aurora", "شفق قطبی", 8, true),
    ROYAL_CRIMSON("royal-crimson", "مخمل شوالیه‌ای", 12, true),
    WARM_SAND("warm-sand", "کویر / شن", 0, false),
    NORDIC_CLEAN("nordic-clean", "نوردیک", 0, false)
}

enum class ReaderCanvasTheme(val id: String, val titleFa: String) {
    CHARCOAL("charcoal", "زغالی"),
    AMOLED("amoled", "AMOLED"),
    MIDNIGHT_SLATE("midnight-slate", "شب اسلیت"),
    SEPIA_PAPER("sepia-paper", "کاغذ کاهی"),
    PURE_LIGHT("pure-light", "صبح مینیمال"),
    PARCHMENT("parchment", "کاغذ کهن"),
    SOFT_MILK("soft-milk", "شیرین لایت"),
    MINT_FRESH("mint-fresh", "نعنایی ملایم")
}

enum class PersianFont(val fontName: String, val titleFa: String) {
    VAZIRMATN("Vazirmatn", "وزیرمتن"),
    SAHEL("Sahel", "ساحل"),
    SHABNAM("Shabnam", "شبنم"),
    MARKAZI("Markazi Text", "مرکزی"),
    AMIRI("Amiri", "امیری"),
    LATEEF("Lateef", "سمرقند"),
    LALEZAR("Lalezar", "لاله‌زار")
}

enum class PosterTemplate(val id: String, val titleFa: String) {
    STORY_9_16("story-9-16", "استوری اینستاگرام (۹:۱۶)"),
    TICKET("ticket", "تیکت داستانی"),
    CYBER_GLASS("cyber-glass", "نئون گلس"),
    IMPERIAL_GOLD("imperial-gold", "کهن‌نامه زرین"),
    DARK_EDITORIAL("dark-editorial", "مینیمال ژورنال")
}

enum class ReadingMode(val id: String, val titleFa: String, val descFa: String, val emoji: String) {
    SCROLL("scroll", "حالت اسکرول", "پیمایش عمودی پیوسته", "📜")
}

data class ReadingPosition(
    val chapterId: Int = 1,
    val scrollOffset: Int = 0,
    val pageIndex: Int = 0,
    val progressPercent: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class ReaderUiSettings(
    val systemTheme: SystemTheme = SystemTheme.TELEGRAM_DARK,
    val readerTheme: ReaderCanvasTheme = ReaderCanvasTheme.CHARCOAL,
    val uiFont: PersianFont = PersianFont.VAZIRMATN,
    val readerFont: PersianFont = PersianFont.VAZIRMATN,
    val fontSizeSp: Float = 20f,
    val lineHeightMultiplier: Float = 2.2f,
    val uiScalePercent: Int = 100,
    val autoScrollSpeed: Int = 1,
    val readingMode: ReadingMode = ReadingMode.SCROLL
)
