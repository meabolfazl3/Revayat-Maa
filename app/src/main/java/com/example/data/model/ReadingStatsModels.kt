package com.example.data.model

data class DailyReadingRecord(
    val date: String, // "yyyy-MM-dd"
    val minutesRead: Int = 0
)

enum class BadgeType(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val accentColorHex: Long
) {
    // 1. Night Owl (30 mins between 00:00 and 04:00 AM, permanently retained)
    NIGHT_OWL(
        id = "night_owl",
        title = "جغد شب",
        description = "ثبت حداقل ۳۰ دقیقه مطالعه فعال بین ساعت ۱۲ شب تا ۴ صبح",
        iconEmoji = "🦉",
        accentColorHex = 0xFF818CF8
    ),

    // 2-7. Progressive Chapter Badges (ماراتن فصول)
    CHAPTER_3(
        id = "chapter_3",
        title = "ماراتن کلمات - آغازگر",
        description = "مطالعه و اتمام ۳ قسمت از رمان",
        iconEmoji = "🌱",
        accentColorHex = 0xFF10B981
    ),
    CHAPTER_5(
        id = "chapter_5",
        title = "خواننده پیگیر",
        description = "مطالعه و اتمام ۵ قسمت از رمان",
        iconEmoji = "📖",
        accentColorHex = 0xFF06B6D4
    ),
    CHAPTER_10(
        id = "chapter_10",
        title = "عاشق داستان",
        description = "مطالعه و اتمام ۱۰ قسمت از رمان",
        iconEmoji = "✨",
        accentColorHex = 0xFF3B82F6
    ),
    CHAPTER_20(
        id = "chapter_20",
        title = "حرفه‌ای کتاب",
        description = "مطالعه و اتمام ۲۰ قسمت از رمان",
        iconEmoji = "📚",
        accentColorHex = 0xFF8B5CF6
    ),
    CHAPTER_50(
        id = "chapter_50",
        title = "بلعنده کتاب",
        description = "مطالعه و اتمام ۵۰ قسمت از رمان",
        iconEmoji = "🐉",
        accentColorHex = 0xFFD946EF
    ),
    CHAPTER_100(
        id = "chapter_100",
        title = "افسانه روایت",
        description = "مطالعه و اتمام ۱۰۰ قسمت از رمان",
        iconEmoji = "👑",
        accentColorHex = 0xFFF59E0B
    ),

    // 8-11. Progressive Reading Time Badges (مدال‌های زمانی)
    TIME_30M(
        id = "time_30m",
        title = "گام نخست",
        description = "ثبت ۳۰ دقیقه زمان کل مطالعه رمان",
        iconEmoji = "⏱️",
        accentColorHex = 0xFF22C55E
    ),
    TIME_3H(
        id = "time_3h",
        title = "غرق در واژه‌ها",
        description = "ثبت ۳ ساعت زمان مطالعه کل در رمان‌خوان",
        iconEmoji = "🌊",
        accentColorHex = 0xFF0EA5E9
    ),
    TIME_10H(
        id = "time_10h",
        title = "کتابخوار تمام‌عیار",
        description = "ثبت ۱۰ ساعت زمان مطالعه عمیق رمان",
        iconEmoji = "🔮",
        accentColorHex = 0xFFA855F7
    ),
    TIME_25H(
        id = "time_25h",
        title = "جاودانه در روایت",
        description = "ثبت ۲۵ ساعت حضور و انس با دنیای داستان",
        iconEmoji = "⚡",
        accentColorHex = 0xFFEC4899
    ),

    // 12-13. Progressive Streak Badges
    STREAK_5(
        id = "streak_5",
        title = "خواننده آتشین",
        description = "رسیدن به زنجیره ۵ روز مطالعه پیوسته",
        iconEmoji = "🔥",
        accentColorHex = 0xFFF97316
    ),
    STREAK_14(
        id = "streak_14",
        title = "مشعل‌دار روایت",
        description = "رسیدن به زنجیره رویایی ۱۴ روز مطالعه پیوسته",
        iconEmoji = "🌟",
        accentColorHex = 0xFFEAB308
    ),

    // 14. Novel Completion Badge
    DIAMOND_LOVER(
        id = "diamond_lover",
        title = "عاشق الماس",
        description = "پایان بردن رمان راز الماس و گشودن آخرین رازها",
        iconEmoji = "💎",
        accentColorHex = 0xFF38BDF8
    ),

    // 15. Early Morning Badge
    EARLY_BIRD(
        id = "early_bird",
        title = "سحرخیز",
        description = "مطالعه کتاب در آرامش صبحگاه (ساعت ۵ تا ۸ صبح)",
        iconEmoji = "🌅",
        accentColorHex = 0xFFF59E0B
    );

    companion object {
        fun fromId(id: String): BadgeType? = when (id) {
            "fire_reader" -> STREAK_5
            "word_marathon" -> CHAPTER_3
            else -> values().firstOrNull { it.id == id }
        }
    }
}

data class ReadingStatsData(
    val totalReadingSeconds: Long = 0L,
    val dailyMinutesMap: Map<String, Int> = emptyMap(), // "yyyy-MM-dd" -> minutes
    val currentStreakDays: Int = 0,
    val lastActiveDate: String = "",
    val chaptersCompletedToday: Set<Int> = emptySet(),
    val totalChaptersCompleted: Set<Int> = emptySet(),
    val totalChaptersReadCount: Int = 0,
    val unlockedBadges: Set<String> = emptySet(),
    val nightMinutesRead: Int = 0,
    val nightOwlUnlocked: Boolean = false,
    val morningMinutesRead: Int = 0
) {
    val totalReadingMinutes: Int get() = (totalReadingSeconds / 60).toInt()
    val totalReadingHours: Int get() = totalReadingMinutes / 60
    val remainingReadingMinutes: Int get() = totalReadingMinutes % 60
}
