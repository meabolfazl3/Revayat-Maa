package com.example.util

import java.util.Calendar
import java.util.TimeZone

object ReleaseSchedule {
    const val BASE_CHAPTER_ID = 14
    private const val BASE_YEAR = 2026
    private const val BASE_MONTH = Calendar.AUGUST // August (0-indexed 7)
    private const val BASE_DAY = 27 // 5 Shahrivar 1405
    private const val BASE_HOUR = 16
    private const val BASE_MINUTE = 0

    /**
     * Calculates the exact release timestamp in milliseconds for a given chapter ID.
     */
    fun getReleaseTimeMillis(chapterId: Int, timeZone: TimeZone = TimeZone.getTimeZone("Asia/Tehran")): Long {
        if (chapterId < BASE_CHAPTER_ID) return 0L
        val cal = Calendar.getInstance(timeZone).apply {
            set(Calendar.YEAR, BASE_YEAR)
            set(Calendar.MONTH, BASE_MONTH)
            set(Calendar.DAY_OF_MONTH, BASE_DAY)
            set(Calendar.HOUR_OF_DAY, BASE_HOUR)
            set(Calendar.MINUTE, BASE_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (chapterId > BASE_CHAPTER_ID) {
                add(Calendar.DAY_OF_YEAR, chapterId - BASE_CHAPTER_ID)
            }
        }
        return cal.timeInMillis
    }

    /**
     * Checks if a chapter has reached its scheduled release date & time.
     */
    fun isChapterReleased(chapterId: Int): Boolean {
        if (chapterId < BASE_CHAPTER_ID) return true // Chapters 1..13 are always available

        val now = System.currentTimeMillis()
        val tehranReleaseMillis = getReleaseTimeMillis(chapterId, TimeZone.getTimeZone("Asia/Tehran"))
        val localReleaseMillis = getReleaseTimeMillis(chapterId, TimeZone.getDefault())

        // Unlock if current time has passed either Tehran time or local device time
        return now >= tehranReleaseMillis || now >= localReleaseMillis
    }

    /**
     * Returns the Persian date string for the chapter's release.
     */
    fun getPersianReleaseDateText(chapterId: Int): String {
        if (chapterId < BASE_CHAPTER_ID) return "منتشر شده"
        val day = 5 + (chapterId - BASE_CHAPTER_ID)
        return if (day <= 31) {
            "$day شهریور"
        } else {
            val mehrDay = day - 31
            "$mehrDay مهر"
        }
    }

    /**
     * Formats user-friendly status badge for chapter cards.
     */
    fun getStatusBadgeText(chapterId: Int, isLocked: Boolean): String {
        if (!isLocked || chapterId < BASE_CHAPTER_ID) {
            return "✨ در دسترس"
        }

        val now = System.currentTimeMillis()
        val tehranReleaseMillis = getReleaseTimeMillis(chapterId, TimeZone.getTimeZone("Asia/Tehran"))
        val diffMillis = tehranReleaseMillis - now

        if (diffMillis <= 0) {
            return "✨ در دسترس"
        }

        val diffHours = diffMillis / (1000 * 60 * 60)
        return when {
            diffHours in 0..12 -> "🔒 امروز ساعت ۱۶:۰۰"
            diffHours in 13..36 -> "🔒 فردا ساعت ۱۶:۰۰"
            else -> "🔒 ${getPersianReleaseDateText(chapterId)} ساعت ۱۶"
        }
    }
}
