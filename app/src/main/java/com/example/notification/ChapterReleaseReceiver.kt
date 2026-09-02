package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.NovelRepository
import com.example.util.ReleaseSchedule

/**
 * BroadcastReceiver responsible for delivering release notifications even when the app is closed.
 * Listens for exact AlarmManager triggers and device reboot / time change events.
 */
class ChapterReleaseReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CHAPTER_RELEASE = "com.revayatemaa.app.ACTION_CHAPTER_RELEASE"
        const val EXTRA_CHAPTER_ID = "extra_chapter_id"
        const val EXTRA_CHAPTER_TITLE = "extra_chapter_title"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                // Re-schedule alarms after device reboot or time change
                NovelNotificationHelper.scheduleAllUpcomingChapterAlarms(context)
            }
            ACTION_CHAPTER_RELEASE -> {
                val chapterId = intent.getIntExtra(EXTRA_CHAPTER_ID, -1)
                val chapterTitle = intent.getStringExtra(EXTRA_CHAPTER_TITLE) ?: "قسمت جدید"

                if (chapterId >= ReleaseSchedule.BASE_CHAPTER_ID) {
                    if (ReleaseSchedule.isChapterReleased(chapterId)) {
                        NovelNotificationHelper.notifyChapterUnlocked(
                            context = context,
                            chapterId = chapterId,
                            chapterTitle = chapterTitle,
                            forceNotify = false
                        )
                    }
                    // Continue scheduling remaining upcoming chapters
                    NovelNotificationHelper.scheduleAllUpcomingChapterAlarms(context)
                }
            }
        }
    }
}
