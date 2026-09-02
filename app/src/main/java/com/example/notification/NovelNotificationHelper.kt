package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.NovelRepository
import com.example.util.ReleaseSchedule

object NovelNotificationHelper {
    const val CHANNEL_ID = "raze_almas_chapter_releases"
    private const val CHANNEL_NAME = "انتشار قسمت‌های جدید رمان"
    private const val CHANNEL_DESC = "اطلاع‌رسانی بازگشایی قسمت‌های جدید رمان راز الماس"
    private const val PREFS_NAME = "raze_almas_notifications"
    private const val KEY_LAST_NOTIFIED = "last_notified_chapter_id"

    const val EXTRA_CHAPTER_ID = "extra_target_chapter_id"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedules background alarms for all upcoming chapters so notifications arrive even if the app is closed.
     */
    fun scheduleAllUpcomingChapterAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val allChapters = NovelRepository.getAllInitialChapters()
        val now = System.currentTimeMillis()

        for (chapter in allChapters) {
            if (chapter.id < ReleaseSchedule.BASE_CHAPTER_ID) continue

            val releaseTimeMillis = ReleaseSchedule.getReleaseTimeMillis(chapter.id)
            if (releaseTimeMillis > now) {
                val intent = Intent(context, ChapterReleaseReceiver::class.java).apply {
                    action = ChapterReleaseReceiver.ACTION_CHAPTER_RELEASE
                    putExtra(ChapterReleaseReceiver.EXTRA_CHAPTER_ID, chapter.id)
                    putExtra(ChapterReleaseReceiver.EXTRA_CHAPTER_TITLE, chapter.title)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    2000 + chapter.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            releaseTimeMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            releaseTimeMillis,
                            pendingIntent
                        )
                    }
                } catch (e: SecurityException) {
                    // Fallback to inexact alarm if exact alarm permission is restricted
                    try {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            releaseTimeMillis,
                            pendingIntent
                        )
                    } catch (_: Exception) {
                    }
                } catch (e: Exception) {
                    Log.e("NovelNotificationHelper", "Failed to schedule alarm for chapter ${chapter.id}", e)
                }
            }
        }
    }

    fun notifyChapterUnlocked(
        context: Context,
        chapterId: Int,
        chapterTitle: String,
        forceNotify: Boolean = false
    ) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastNotified = prefs.getInt(KEY_LAST_NOTIFIED, 13) // Chapters 1-13 unlocked by default

        if (!forceNotify && chapterId <= lastNotified) {
            return
        }

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_CHAPTER_ID, chapterId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            chapterId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "💎 خبر خوش! قسمت جدید رمان باز شد"
        val message = "سلام همراه عزیز! قسمت $chapterId («$chapterTitle») هم اکنون در رمان راز الماس در دسترس شماست. بفرمایید به ادامه ماجراجویی مهیج آریا و سارا!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            val manager = NotificationManagerCompat.from(context)
            if (manager.areNotificationsEnabled()) {
                manager.notify(1000 + chapterId, notification)
                prefs.edit().putInt(KEY_LAST_NOTIFIED, chapterId).apply()
            }
        } catch (e: SecurityException) {
            // Permission not granted on Android 13+
        }
    }
}
