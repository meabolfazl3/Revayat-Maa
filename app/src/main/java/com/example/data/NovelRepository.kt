package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Bookmark
import com.example.data.model.Chapter
import com.example.data.model.PersianFont
import com.example.data.model.ReaderCanvasTheme
import com.example.data.model.ReaderUiSettings
import com.example.data.model.ReadingPosition
import com.example.data.model.SystemTheme
import com.example.util.ReleaseSchedule
import org.json.JSONArray
import org.json.JSONObject

class NovelRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("raze_almas_prefs", Context.MODE_PRIVATE)

    companion object {
        const val APP_NAME = "روایت ما"
        const val PRODUCER_NAME = "ابوالفضل پورنجف"
        const val NOVEL_TITLE = "راز الماس"
        const val NOVEL_AUTHOR = "ابوالفضل پورنجف"
        const val NOVEL_CHANNEL = "https://eitaa.com/revayate_maa"
        const val TOTAL_CHAPTERS = 39

        fun getAllInitialChapters(): List<Chapter> =
            ChapterListPart1.chapters + ChapterListPart2.chapters + ChapterListPart3.chapters

        private const val KEY_CURRENT_CHAPTER = "current_chapter"
        private const val KEY_SETTINGS_SYS_THEME = "settings_sys_theme"
        private const val KEY_SETTINGS_READER_THEME = "settings_reader_theme"
        private const val KEY_SETTINGS_UI_FONT = "settings_ui_font"
        private const val KEY_SETTINGS_READER_FONT = "settings_reader_font"
        private const val KEY_SETTINGS_FONT_SIZE = "settings_font_size"
        private const val KEY_SETTINGS_LINE_HEIGHT = "settings_line_height"
        private const val KEY_SETTINGS_UI_SCALE = "settings_ui_scale"
        private const val KEY_SETTINGS_SCROLL_SPEED = "settings_scroll_speed"
        private const val KEY_SETTINGS_READING_MODE = "settings_reading_mode"
        private const val KEY_PATCHED_CHAPTERS = "patched_chapters"
        private const val KEY_BOOKMARKS = "bookmarks"
        private const val KEY_ALL_CHAPTERS_UNLOCKED = "all_chapters_unlocked"
        private const val KEY_LAST_READ_TIMESTAMP = "last_read_timestamp"
        private const val PREFIX_CHAPTER_POS = "pos_chap_"
    }

    fun verifyPasscode(input: String): Boolean {
        val clean = input.trim()
        if (clean.length != 8) return false
        // Obfuscated mask for "03130110" with key 0x5A
        val mask = intArrayOf(106, 105, 107, 105, 106, 107, 107, 106)
        val key = 0x5A
        for (i in clean.indices) {
            if ((clean[i].code xor key) != mask[i]) {
                return false
            }
        }
        return true
    }

    private val initialChapters: List<Chapter> = 
        ChapterListPart1.chapters + ChapterListPart2.chapters + ChapterListPart3.chapters

    fun isAllChaptersUnlocked(): Boolean {
        return prefs.getBoolean(KEY_ALL_CHAPTERS_UNLOCKED, false)
    }

    fun setAllChaptersUnlocked(unlocked: Boolean) {
        prefs.edit().putBoolean(KEY_ALL_CHAPTERS_UNLOCKED, unlocked).apply()
    }

    fun getChapters(): List<Chapter> {
        val isAllUnlocked = isAllChaptersUnlocked()
        val patchedMap = loadPatchedChapters()
        return initialChapters.map { ch ->
            val base = patchedMap[ch.id] ?: ch
            val isLocked = when {
                isAllUnlocked -> false
                patchedMap.containsKey(ch.id) -> base.isLocked
                ReleaseSchedule.isChapterReleased(ch.id) -> false
                else -> base.isLocked
            }
            base.copy(isLocked = isLocked)
        }
    }

    fun getPatchedChapterIds(): Set<Int> {
        return loadPatchedChapters().keys
    }

    fun savePatchedChapter(chapter: Chapter) {
        val patchedMap = loadPatchedChapters().toMutableMap()
        patchedMap[chapter.id] = chapter
        savePatchedChapters(patchedMap)
    }

    fun removePatchedChapter(chapterId: Int) {
        val patchedMap = loadPatchedChapters().toMutableMap()
        patchedMap.remove(chapterId)
        savePatchedChapters(patchedMap)
    }

    private fun loadPatchedChapters(): Map<Int, Chapter> {
        val jsonStr = prefs.getString(KEY_PATCHED_CHAPTERS, null) ?: return emptyMap()
        val map = mutableMapOf<Int, Chapter>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getInt("id")
                val title = obj.getString("title")
                val isLocked = obj.optBoolean("isLocked", false)
                val contentArray = obj.getJSONArray("content")
                val content = mutableListOf<String>()
                for (j in 0 until contentArray.length()) {
                    content.add(contentArray.getString(j))
                }
                map[id] = Chapter(id = id, title = title, isLocked = isLocked, content = content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    private fun savePatchedChapters(map: Map<Int, Chapter>) {
        try {
            val jsonArray = JSONArray()
            map.values.forEach { ch ->
                val obj = JSONObject().apply {
                    put("id", ch.id)
                    put("title", ch.title)
                    put("isLocked", ch.isLocked)
                    val contentArr = JSONArray()
                    ch.content.forEach { contentArr.put(it) }
                    put("content", contentArr)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_PATCHED_CHAPTERS, jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSavedChapterIndex(): Int {
        return prefs.getInt(KEY_CURRENT_CHAPTER, 0)
    }

    fun saveCurrentChapterIndex(index: Int) {
        prefs.edit().putInt(KEY_CURRENT_CHAPTER, index).apply()
    }

    fun getSettings(): ReaderUiSettings {
        val sysThemeName = prefs.getString(KEY_SETTINGS_SYS_THEME, SystemTheme.TELEGRAM_DARK.name) ?: SystemTheme.TELEGRAM_DARK.name
        val readerThemeName = prefs.getString(KEY_SETTINGS_READER_THEME, ReaderCanvasTheme.CHARCOAL.name) ?: ReaderCanvasTheme.CHARCOAL.name
        val uiFontName = prefs.getString(KEY_SETTINGS_UI_FONT, PersianFont.VAZIRMATN.name) ?: PersianFont.VAZIRMATN.name
        val readerFontName = prefs.getString(KEY_SETTINGS_READER_FONT, PersianFont.VAZIRMATN.name) ?: PersianFont.VAZIRMATN.name
        val fontSize = prefs.getFloat(KEY_SETTINGS_FONT_SIZE, 20f)
        val lineHeight = prefs.getFloat(KEY_SETTINGS_LINE_HEIGHT, 2.2f)
        val uiScale = prefs.getInt(KEY_SETTINGS_UI_SCALE, 100)
        val scrollSpeed = prefs.getInt(KEY_SETTINGS_SCROLL_SPEED, 1)
        val readingModeName = prefs.getString(KEY_SETTINGS_READING_MODE, com.example.data.model.ReadingMode.SCROLL.name) ?: com.example.data.model.ReadingMode.SCROLL.name

        return ReaderUiSettings(
            systemTheme = runCatching { SystemTheme.valueOf(sysThemeName) }.getOrDefault(SystemTheme.TELEGRAM_DARK),
            readerTheme = runCatching { ReaderCanvasTheme.valueOf(readerThemeName) }.getOrDefault(ReaderCanvasTheme.CHARCOAL),
            uiFont = runCatching { PersianFont.valueOf(uiFontName) }.getOrDefault(PersianFont.VAZIRMATN),
            readerFont = runCatching { PersianFont.valueOf(readerFontName) }.getOrDefault(PersianFont.VAZIRMATN),
            fontSizeSp = fontSize,
            lineHeightMultiplier = lineHeight,
            uiScalePercent = uiScale,
            autoScrollSpeed = scrollSpeed,
            readingMode = runCatching { com.example.data.model.ReadingMode.valueOf(readingModeName) }.getOrDefault(com.example.data.model.ReadingMode.SCROLL)
        )
    }

    fun saveSettings(settings: ReaderUiSettings) {
        prefs.edit()
            .putString(KEY_SETTINGS_SYS_THEME, settings.systemTheme.name)
            .putString(KEY_SETTINGS_READER_THEME, settings.readerTheme.name)
            .putString(KEY_SETTINGS_UI_FONT, settings.uiFont.name)
            .putString(KEY_SETTINGS_READER_FONT, settings.readerFont.name)
            .putFloat(KEY_SETTINGS_FONT_SIZE, settings.fontSizeSp)
            .putFloat(KEY_SETTINGS_LINE_HEIGHT, settings.lineHeightMultiplier)
            .putInt(KEY_SETTINGS_UI_SCALE, settings.uiScalePercent)
            .putInt(KEY_SETTINGS_SCROLL_SPEED, settings.autoScrollSpeed)
            .putString(KEY_SETTINGS_READING_MODE, settings.readingMode.name)
            .apply()
    }

    fun getBookmarks(): List<Bookmark> {
        val jsonStr = prefs.getString(KEY_BOOKMARKS, null) ?: return emptyList()
        val list = mutableListOf<Bookmark>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Bookmark(
                        id = obj.getString("id"),
                        chapterId = obj.getInt("chapterId"),
                        chapterTitle = obj.getString("chapterTitle"),
                        paragraphIndex = obj.getInt("paragraphIndex"),
                        text = obj.getString("text"),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun addBookmark(bookmark: Bookmark) {
        val list = getBookmarks().toMutableList()
        if (list.none { it.chapterId == bookmark.chapterId && it.paragraphIndex == bookmark.paragraphIndex }) {
            list.add(0, bookmark)
            saveBookmarks(list)
        }
    }

    fun removeBookmark(bookmarkId: String) {
        val list = getBookmarks().filterNot { it.id == bookmarkId }
        saveBookmarks(list)
    }

    private fun saveBookmarks(list: List<Bookmark>) {
        try {
            val jsonArray = JSONArray()
            list.forEach { bm ->
                val obj = JSONObject().apply {
                    put("id", bm.id)
                    put("chapterId", bm.chapterId)
                    put("chapterTitle", bm.chapterTitle)
                    put("paragraphIndex", bm.paragraphIndex)
                    put("text", bm.text)
                    put("timestamp", bm.timestamp)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_BOOKMARKS, jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveChapterReadingPosition(chapterId: Int, scrollOffset: Int, pageIndex: Int, progressPercent: Int) {
        try {
            val obj = JSONObject().apply {
                put("chapterId", chapterId)
                put("scrollOffset", scrollOffset)
                put("pageIndex", pageIndex)
                put("progressPercent", progressPercent)
                put("lastUpdated", System.currentTimeMillis())
            }
            prefs.edit()
                .putString("$PREFIX_CHAPTER_POS$chapterId", obj.toString())
                .putLong(KEY_LAST_READ_TIMESTAMP, System.currentTimeMillis())
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getChapterReadingPosition(chapterId: Int): ReadingPosition {
        val jsonStr = prefs.getString("$PREFIX_CHAPTER_POS$chapterId", null) ?: return ReadingPosition(chapterId = chapterId)
        return try {
            val obj = JSONObject(jsonStr)
            ReadingPosition(
                chapterId = obj.optInt("chapterId", chapterId),
                scrollOffset = obj.optInt("scrollOffset", 0),
                pageIndex = obj.optInt("pageIndex", 0),
                progressPercent = obj.optInt("progressPercent", 0),
                lastUpdated = obj.optLong("lastUpdated", 0L)
            )
        } catch (e: Exception) {
            ReadingPosition(chapterId = chapterId)
        }
    }
}

