package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NovelRepository
import com.example.data.model.Bookmark
import com.example.data.model.Chapter
import com.example.data.model.ReaderUiSettings
import com.example.data.model.ReadingPosition
import com.example.notification.NovelNotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScreenMode {
    SHELF,
    BOOK_DETAILS,
    READER
}

data class ReaderUiState(
    val currentScreen: ScreenMode = ScreenMode.SHELF,
    val chapters: List<Chapter> = emptyList(),
    val patchedChapterIds: Set<Int> = emptySet(),
    val currentChapterIndex: Int = 11, // Default Chapter 12
    val settings: ReaderUiSettings = ReaderUiSettings(),
    val bookmarks: List<Bookmark> = emptyList(),
    val isAutoScrolling: Boolean = false,
    val isFocusMode: Boolean = false,
    val readingProgressPercent: Int = 0,
    val selectedParagraphText: String = "",
    val selectedParagraphIndex: Int? = null,
    val restorePositionTimestamp: Long = 0L,
    val showSystemSettingsDialog: Boolean = false,
    val showReaderSettingsDialog: Boolean = false,
    val showQuotePosterDialog: Boolean = false,
    val showPatchImportDialog: Boolean = false,
    val showBookmarksDialog: Boolean = false,
    val showAboutUsDialog: Boolean = false,
    val showContactUsDialog: Boolean = false,
    val showUnlockCodeDialog: Boolean = false,
    val showUnlockSuccessPopup: Boolean = false,
    val isAllUnlocked: Boolean = false,
    val searchDrawerQuery: String = "",
    val toastMessage: String? = null
)

class ReaderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NovelRepository(application)

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        loadData()
        startPeriodicReleaseCheck()
    }

    fun refreshChapters() {
        val chapters = repository.getChapters()
        val patchedIds = repository.getPatchedChapterIds()
        val isAllUnlocked = repository.isAllChaptersUnlocked()
        val current = _uiState.value
        if (current.chapters != chapters || current.patchedChapterIds != patchedIds || current.isAllUnlocked != isAllUnlocked) {
            _uiState.update {
                it.copy(
                    chapters = chapters,
                    patchedChapterIds = patchedIds,
                    isAllUnlocked = isAllUnlocked
                )
            }
        }
    }

    private fun startPeriodicReleaseCheck() {
        viewModelScope.launch {
            while (true) {
                delay(60_000)
                refreshChapters()
                checkAndNotifyNewUnlockedChapters()
            }
        }
    }

    private fun loadData() {
        val chapters = repository.getChapters()
        val patchedIds = repository.getPatchedChapterIds()
        val savedIndex = repository.getSavedChapterIndex().coerceIn(0, chapters.lastIndex)
        val settings = repository.getSettings()
        val bookmarks = repository.getBookmarks()
        val isAllUnlocked = repository.isAllChaptersUnlocked()

        _uiState.update {
            it.copy(
                chapters = chapters,
                patchedChapterIds = patchedIds,
                currentChapterIndex = savedIndex,
                settings = settings,
                bookmarks = bookmarks,
                isAllUnlocked = isAllUnlocked
            )
        }
    }

    fun selectChapter(index: Int) {
        val chapters = _uiState.value.chapters
        if (index in chapters.indices) {
            val targetChapter = chapters[index]
            if (targetChapter.isLocked) {
                showToast("🔒 قسمت «${targetChapter.title}» هنوز قفل است و به‌زودی در کانال «روایت ما» منتشر خواهد شد.")
                return
            }
            repository.saveCurrentChapterIndex(index)
            _uiState.update {
                it.copy(
                    currentChapterIndex = index,
                    selectedParagraphIndex = null,
                    selectedParagraphText = "",
                    readingProgressPercent = 0
                )
            }
        }
    }

    fun selectChapterById(chapterId: Int) {
        val chapters = _uiState.value.chapters
        val index = chapters.indexOfFirst { it.id == chapterId }
        if (index >= 0) {
            selectChapter(index)
        }
    }

    fun checkAndNotifyNewUnlockedChapters() {
        val chapters = _uiState.value.chapters
        val latestUnlocked = chapters.filter { !it.isLocked }.maxByOrNull { it.id }
        if (latestUnlocked != null && latestUnlocked.id > 13) {
            NovelNotificationHelper.notifyChapterUnlocked(
                getApplication(),
                latestUnlocked.id,
                latestUnlocked.title,
                forceNotify = false
            )
        }
    }

    fun navigateChapter(delta: Int) {
        val current = _uiState.value.currentChapterIndex
        val target = current + delta
        selectChapter(target)
    }

    fun updateSettings(newSettings: ReaderUiSettings) {
        repository.saveSettings(newSettings)
        _uiState.update { it.copy(settings = newSettings) }
    }

    fun toggleAutoScroll() {
        val newState = !_uiState.value.isAutoScrolling
        _uiState.update {
            it.copy(
                isAutoScrolling = newState,
                toastMessage = if (newState) "پیمایش خودکار فعال شد" else "پیمایش خودکار متوقف شد"
            )
        }
    }

    fun adjustAutoScrollSpeed(delta: Int) {
        val currentSpeed = _uiState.value.settings.autoScrollSpeed
        val newSpeed = (currentSpeed + delta).coerceIn(1, 5)
        val updatedSettings = _uiState.value.settings.copy(autoScrollSpeed = newSpeed)
        updateSettings(updatedSettings)
        showToast("سرعت اسکرول: ${newSpeed}x")
    }

    fun toggleFocusMode() {
        val newFocus = !_uiState.value.isFocusMode
        _uiState.update { it.copy(isFocusMode = newFocus) }
    }

    fun selectParagraph(index: Int, text: String) {
        _uiState.update {
            it.copy(
                selectedParagraphIndex = index,
                selectedParagraphText = text
            )
        }
    }

    fun clearParagraphSelection() {
        _uiState.update {
            it.copy(
                selectedParagraphIndex = null,
                selectedParagraphText = ""
            )
        }
    }

    fun addBookmarkForCurrentSelection() {
        val state = _uiState.value
        val pIndex = state.selectedParagraphIndex ?: return
        val currentCh = state.chapters[state.currentChapterIndex]
        val text = state.selectedParagraphText

        val bookmark = Bookmark(
            chapterId = currentCh.id,
            chapterTitle = currentCh.title,
            paragraphIndex = pIndex,
            text = text
        )
        repository.addBookmark(bookmark)
        val updated = repository.getBookmarks()
        _uiState.update {
            it.copy(
                bookmarks = updated,
                selectedParagraphIndex = null,
                toastMessage = "🔖 پاراگراف به نشانک‌ها اضافه شد"
            )
        }
    }

    fun removeBookmark(id: String) {
        repository.removeBookmark(id)
        val updated = repository.getBookmarks()
        _uiState.update {
            it.copy(
                bookmarks = updated,
                toastMessage = "نشانک حذف شد"
            )
        }
    }

    fun importChapterPatch(chapter: Chapter) {
        repository.savePatchedChapter(chapter)
        val updatedChapters = repository.getChapters()
        val patchedIds = repository.getPatchedChapterIds()
        val targetIndex = updatedChapters.indexOfFirst { it.id == chapter.id }
        val finalIndex = if (targetIndex >= 0) targetIndex else _uiState.value.currentChapterIndex

        repository.saveCurrentChapterIndex(finalIndex)
        _uiState.update {
            it.copy(
                chapters = updatedChapters,
                patchedChapterIds = patchedIds,
                currentChapterIndex = finalIndex,
                showPatchImportDialog = false
            )
        }

        NovelNotificationHelper.notifyChapterUnlocked(
            getApplication(),
            chapter.id,
            chapter.title,
            forceNotify = true
        )

        showToast("🎉 قسمت ${chapter.id} («${chapter.title}») بازگشایی شد! مطالعه دلنشینی داشته باشید 💎")
    }

    fun deleteChapterPatch(chapterId: Int) {
        repository.removePatchedChapter(chapterId)
        val updatedChapters = repository.getChapters()
        val patchedIds = repository.getPatchedChapterIds()
        val currentIdx = _uiState.value.currentChapterIndex
        val currentCh = _uiState.value.chapters.getOrNull(currentIdx)

        // If the deleted chapter was open, fallback to chapter 12 (index 11) or first unlocked chapter
        val newIndex = if (currentCh?.id == chapterId) {
            val fallback = updatedChapters.indexOfLast { !it.isLocked }.coerceAtLeast(0)
            repository.saveCurrentChapterIndex(fallback)
            fallback
        } else {
            currentIdx.coerceIn(0, updatedChapters.lastIndex)
        }

        _uiState.update {
            it.copy(
                chapters = updatedChapters,
                patchedChapterIds = patchedIds,
                currentChapterIndex = newIndex
            )
        }
        showToast("🗑️ پچ قسمت حذف شد و مجدداً قفل شد 🔒")
    }

    fun setReadingProgress(percent: Int) {
        _uiState.update { it.copy(readingProgressPercent = percent.coerceIn(0, 100)) }
    }

    fun setSearchDrawerQuery(query: String) {
        _uiState.update { it.copy(searchDrawerQuery = query) }
    }

    fun setSystemSettingsDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showSystemSettingsDialog = visible) }
    }

    fun setReaderSettingsDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showReaderSettingsDialog = visible) }
    }

    fun setQuotePosterDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showQuotePosterDialog = visible) }
    }

    fun saveReadingPosition(chapterId: Int, scrollOffset: Int, pageIndex: Int, progressPercent: Int) {
        repository.saveChapterReadingPosition(chapterId, scrollOffset, pageIndex, progressPercent)
        _uiState.update { it.copy(readingProgressPercent = progressPercent) }
    }

    fun getSavedReadingPosition(chapterId: Int): ReadingPosition {
        return repository.getChapterReadingPosition(chapterId)
    }

    fun navigateToShelf() {
        _uiState.update { it.copy(currentScreen = ScreenMode.SHELF) }
    }

    fun navigateToBookDetails() {
        _uiState.update { it.copy(currentScreen = ScreenMode.BOOK_DETAILS) }
    }

    fun navigateToHome() {
        _uiState.update { it.copy(currentScreen = ScreenMode.BOOK_DETAILS) }
    }

    fun navigateToReader(chapterIndex: Int = -1, restorePosition: Boolean = true) {
        if (chapterIndex >= 0) {
            selectChapter(chapterIndex)
        }
        _uiState.update { 
            it.copy(
                currentScreen = ScreenMode.READER,
                restorePositionTimestamp = if (restorePosition) System.currentTimeMillis() else 0L
            ) 
        }
    }

    fun setAboutUsDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showAboutUsDialog = visible) }
    }

    fun setContactUsDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showContactUsDialog = visible) }
    }

    fun setPatchImportDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showPatchImportDialog = visible) }
    }

    fun setBookmarksDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showBookmarksDialog = visible) }
    }

    fun setUnlockCodeDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showUnlockCodeDialog = visible) }
    }

    fun setUnlockSuccessPopupVisible(visible: Boolean) {
        _uiState.update { it.copy(showUnlockSuccessPopup = visible) }
    }

    fun submitUnlockCode(rawCode: String): Boolean {
        val persian = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        val arabic = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
        var normalized = rawCode.trim()
        for (i in 0..9) {
            normalized = normalized
                .replace(persian[i], i.toString())
                .replace(arabic[i], i.toString())
        }
        normalized = normalized.filter { it.isDigit() }

        if (repository.verifyPasscode(normalized)) {
            repository.setAllChaptersUnlocked(true)
            val updatedChapters = repository.getChapters()
            _uiState.update {
                it.copy(
                    chapters = updatedChapters,
                    isAllUnlocked = true,
                    showUnlockCodeDialog = false,
                    showUnlockSuccessPopup = true
                )
            }
            return true
        } else {
            showToast("کد وارد شده صحیح نمی‌باشد. لطفاً مجدداً بررسی نمایید.")
            return false
        }
    }

    fun resetUnlockedState() {
        repository.setAllChaptersUnlocked(false)
        val updatedChapters = repository.getChapters()
        _uiState.update {
            it.copy(
                chapters = updatedChapters,
                isAllUnlocked = false
            )
        }
        showToast("🔒 وضعیت قفل قسمت‌ها بازنشانی شد.")
    }

    private var toastJob: kotlinx.coroutines.Job? = null

    fun showToast(msg: String) {
        toastJob?.cancel()
        _uiState.update { it.copy(toastMessage = msg) }
        toastJob = viewModelScope.launch {
            kotlinx.coroutines.delay(2800)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }

    fun clearToast() {
        toastJob?.cancel()
        _uiState.update { it.copy(toastMessage = null) }
    }
}
