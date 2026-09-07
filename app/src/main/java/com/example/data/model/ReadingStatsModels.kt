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
    NIGHT_OWL(
        id = "night_owl",
        title = "جغد شب",
        description = "مطالعه بین ساعت ۱۲ شب تا ۴ صبح (حداقل ۱۰ دقیقه)",
        iconEmoji = "🦉",
        accentColorHex = 0xFF818CF8
    ),
    FIRE_READER(
        id = "fire_reader",
        title = "خواننده آتشین",
        description = "رسیدن به زنجیره ۵ روز مطالعه پیوسته",
        iconEmoji = "🔥",
        accentColorHex = 0xFFF97316
    ),
    WORD_MARATHON(
        id = "word_marathon",
        title = "ماراتن کلمات",
        description = "تمام کردن مطالعه ۳ قسمت رمان در یک روز",
        iconEmoji = "🏃‍♂️",
        accentColorHex = 0xFF10B981
    ),
    DIAMOND_LOVER(
        id = "diamond_lover",
        title = "عاشق الماس",
        description = "مطالعه بیش از ۲ ساعت از رمان راز الماس",
        iconEmoji = "💎",
        accentColorHex = 0xFF38BDF8
    ),
    EARLY_BIRD(
        id = "early_bird",
        title = "سحرخیز",
        description = "مطالعه کتاب در ساعت ۵ تا ۸ صبح",
        iconEmoji = "🌅",
        accentColorHex = 0xFFF59E0B
    );

    companion object {
        fun fromId(id: String): BadgeType? = values().firstOrNull { it.id == id }
    }
}

data class ReadingStatsData(
    val totalReadingSeconds: Long = 0L,
    val dailyMinutesMap: Map<String, Int> = emptyMap(), // "yyyy-MM-dd" -> minutes
    val currentStreakDays: Int = 0,
    val lastActiveDate: String = "",
    val chaptersCompletedToday: Set<Int> = emptySet(),
    val unlockedBadges: Set<String> = emptySet(),
    val nightMinutesRead: Int = 0,
    val morningMinutesRead: Int = 0
) {
    val totalReadingMinutes: Int get() = (totalReadingSeconds / 60).toInt()
    val totalReadingHours: Int get() = totalReadingMinutes / 60
    val remainingReadingMinutes: Int get() = totalReadingMinutes % 60
}
