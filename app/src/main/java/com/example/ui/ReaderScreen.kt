package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.withFrameNanos
import com.example.data.NovelRepository
import com.example.data.model.Chapter
import com.example.ui.components.AppLogo
import com.example.ui.components.BadgeCelebrationDialog
import com.example.ui.components.BookmarksDialog
import com.example.ui.components.PatchImportDialog
import com.example.ui.components.QuotePosterDialog
import com.example.ui.components.ReaderDock
import com.example.ui.components.ReaderSettingsDialog
import com.example.ui.components.SystemSettingsDialog
import com.example.ui.components.persianNumber
import com.example.ui.theme.NovelThemes
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(viewModel: ReaderViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val sysColors = NovelThemes.getSystemColors(uiState.settings.systemTheme)
    val readerColors = NovelThemes.getReaderColors(uiState.settings.readerTheme)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val currentChapter = uiState.chapters.getOrNull(uiState.currentChapterIndex)
        ?: Chapter(1, "بدون عنوان", false, emptyList())

    // Ultra-smooth frame-synchronized auto-scroll engine (requestAnimationFrame equivalent)
    LaunchedEffect(uiState.isAutoScrolling, uiState.settings.autoScrollSpeed) {
        if (uiState.isAutoScrolling) {
            // Speed 1: ~32 pixels/second (gentle, relaxing reading pace)
            val pixelsPerSecond = when (uiState.settings.autoScrollSpeed) {
                1 -> 32f
                2 -> 56f
                3 -> 88f
                4 -> 128f
                5 -> 175f
                else -> (uiState.settings.autoScrollSpeed * 32f)
            }
            var lastFrameNanos = 0L
            while (isActive && uiState.isAutoScrolling) {
                withFrameNanos { frameTimeNanos ->
                    if (lastFrameNanos > 0L) {
                        val dt = (frameTimeNanos - lastFrameNanos) / 1_000_000_000f
                        val delta = pixelsPerSecond * dt
                        if (scrollState.value < scrollState.maxValue) {
                            scrollState.dispatchRawDelta(delta)
                        }
                    }
                    lastFrameNanos = frameTimeNanos
                }
            }
        }
    }

    // Live Reading Percentage Calculation
    val readingPercent by remember {
        derivedStateOf {
            if (scrollState.maxValue > 0) {
                ((scrollState.value.toFloat() / scrollState.maxValue.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
        }
    }

    // Exact Reading Position Restoration when chapter opens or on "Continue Reading"
    LaunchedEffect(currentChapter.id, uiState.restorePositionTimestamp) {
        val saved = viewModel.getSavedReadingPosition(currentChapter.id)
        if (saved.scrollOffset > 0) {
            // Wait for layout frame to guarantee content height is computed
            withFrameNanos { }
            scrollState.scrollTo(saved.scrollOffset)
        } else {
            scrollState.scrollTo(0)
        }
    }

    // Live Debounced Reading Position Saver (Scroll Mode)
    LaunchedEffect(scrollState.value, currentChapter.id) {
        if (currentChapter.content.isNotEmpty()) {
            delay(150) // 150ms debounce
            viewModel.saveReadingPosition(
                chapterId = currentChapter.id,
                scrollOffset = scrollState.value,
                pageIndex = 0,
                progressPercent = readingPercent
            )
        }
    }

    // Toast auto-clear
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            delay(2800)
            viewModel.clearToast()
        }
    }

    // Active reading timer: tracks seconds while reader is on screen
    LaunchedEffect(currentChapter.id) {
        while (isActive) {
            delay(10_000L) // every 10 seconds
            viewModel.recordReadingSeconds(10L)
        }
    }

    // Track unique chapters completed in this reader session to prevent duplicate triggers
    val completedChaptersInSession = remember { mutableSetOf<Int>() }

    // Auto-record chapter finished strictly once when reaching the actual bottom of the chapter
    LaunchedEffect(currentChapter.id) {
        snapshotFlow {
            val maxScroll = scrollState.maxValue
            maxScroll > 0 && scrollState.value >= (maxScroll - 50)
        }.collect { reachedBottom ->
            if (reachedBottom && !completedChaptersInSession.contains(currentChapter.id)) {
                completedChaptersInSession.add(currentChapter.id)
                viewModel.recordChapterFinished(currentChapter.id)
            }
        }
    }

    // Word count & estimated reading time calculation (Words / 200)
    val chapterWordCount = remember(currentChapter.id, currentChapter.content) {
        currentChapter.content.sumOf { p ->
            p.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
        }
    }
    val estimatedReadMinutes = remember(chapterWordCount) {
        (chapterWordCount / 200).coerceAtLeast(1)
    }

    // Step-by-step Back Button Handling (closes open drawers/popups/dialogs or returns to Home)
    BackHandler {
        when {
            drawerState.isOpen -> {
                scope.launch { drawerState.close() }
            }
            uiState.showSystemSettingsDialog -> {
                viewModel.setSystemSettingsDialogVisible(false)
            }
            uiState.showReaderSettingsDialog -> {
                viewModel.setReaderSettingsDialogVisible(false)
            }
            uiState.showQuotePosterDialog -> {
                viewModel.setQuotePosterDialogVisible(false)
            }
            uiState.showPatchImportDialog -> {
                viewModel.setPatchImportDialogVisible(false)
            }
            uiState.showBookmarksDialog -> {
                viewModel.setBookmarksDialogVisible(false)
            }
            uiState.newlyUnlockedBadge != null -> {
                viewModel.dismissBadgeCelebration()
            }
            uiState.isFocusMode -> {
                viewModel.toggleFocusMode()
            }
            uiState.selectedParagraphIndex != null -> {
                viewModel.clearParagraphSelection()
            }
            else -> {
                viewModel.navigateToHome()
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !uiState.isFocusMode,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = sysColors.surface,
                    drawerContentColor = sysColors.text,
                    modifier = Modifier
                        .widthIn(max = 330.dp)
                        .fillMaxHeight()
                        .testTag("table_of_contents_drawer")
                ) {
                    DrawerTableOfContents(
                        chapters = uiState.chapters,
                        patchedChapterIds = uiState.patchedChapterIds,
                        currentChapterIndex = uiState.currentChapterIndex,
                        searchQuery = uiState.searchDrawerQuery,
                        sysColors = sysColors,
                        onSearchChange = viewModel::setSearchDrawerQuery,
                        onSelectChapter = { index ->
                            scope.launch { drawerState.close() }
                            viewModel.selectChapter(index)
                        },
                        onDeleteChapterPatch = viewModel::deleteChapterPatch,
                        onCloseDrawer = { scope.launch { drawerState.close() } }
                    )
                }
            }
        ) {
            Scaffold(
                containerColor = readerColors.bg,
                topBar = {
                    if (!uiState.isFocusMode) {
                        Column {
                            // Top Reading Progress Bar (Glowing gradient line)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.5.dp)
                                    .background(sysColors.bg)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = (readingPercent / 100f).coerceIn(0f, 1f))
                                        .height(3.5.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(sysColors.primary, sysColors.accent)
                                            )
                                        )
                                        .testTag("reading_progress_bar")
                                )
                            }

                            // Header TopAppBar
                            CenterAlignedTopAppBar(
                                title = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.clickable {
                                            scope.launch { drawerState.open() }
                                        }
                                    ) {
                                        AppLogo(
                                            modifier = Modifier.size(24.dp),
                                            shapeRadius = 6.dp
                                        )
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = NovelRepository.NOVEL_TITLE,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = sysColors.text
                                            )
                                            Text(
                                                text = currentChapter.title,
                                                fontSize = 12.sp,
                                                color = sysColors.accent,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                },
                                // In RTL layout, navigationIcon is at the START (RIGHT side)
                                navigationIcon = {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                        modifier = Modifier.testTag("open_drawer_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "فهرست قسمت‌های رمان",
                                            tint = sysColors.text
                                        )
                                    }
                                },
                                // In RTL layout, actions are at the END (LEFT side)
                                actions = {
                                    IconButton(
                                        onClick = { viewModel.navigateToHome() },
                                        modifier = Modifier.testTag("back_to_home_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "بازگشت به کتابخانه",
                                            tint = sysColors.text
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = sysColors.surface
                                )
                            )
                            HorizontalDivider(color = sysColors.border, thickness = 1.dp)
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(readerColors.bg)
                ) {
                    // Continuous Vertical Scroll Reading Container
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Hero Chapter Banner
                        Surface(
                            color = readerColors.badgeBg,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, readerColors.badgeText.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "رمان ${NovelRepository.NOVEL_TITLE} اثر ${NovelRepository.NOVEL_AUTHOR}",
                                color = readerColors.badgeText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = currentChapter.title,
                            color = readerColors.title,
                            fontSize = 24.sp,
                            fontFamily = NovelThemes.getFontFamily(uiState.settings.readerFont),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("chapter_main_title")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Time-to-Read Capsule Badge
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = readerColors.badgeBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, readerColors.badgeText.copy(alpha = 0.35f)),
                            modifier = Modifier.testTag("time_to_read_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "⏱️ زمان تقریبی مطالعه: ${persianNumber(estimatedReadMinutes)} دقیقه (${persianNumber(chapterWordCount)} کلمه)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = readerColors.badgeText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(
                            color = sysColors.border,
                            thickness = 1.dp,
                            modifier = Modifier.widthIn(max = 600.dp)
                        )
                        Spacer(modifier = Modifier.height(22.dp))

                        // Paragraphs List - Continuous Single Reader Container
                        Column(
                            modifier = Modifier
                                .widthIn(max = 760.dp)
                                .fillMaxWidth()
                                .testTag("readerContent")
                        ) {
                            currentChapter.content.forEachIndexed { pIdx, paragraph ->
                                val isBookmarked = uiState.bookmarks.any {
                                    it.chapterId == currentChapter.id && it.paragraphIndex == pIdx
                                }
                                val isSelected = uiState.selectedParagraphIndex == pIdx

                                ParagraphCard(
                                    paragraphText = paragraph,
                                    index = pIdx,
                                    isSelected = isSelected,
                                    isBookmarked = isBookmarked,
                                    readerColors = readerColors,
                                    sysColors = sysColors,
                                    fontSizeSp = uiState.settings.fontSizeSp,
                                    lineHeightMultiplier = uiState.settings.lineHeightMultiplier,
                                    readerFont = uiState.settings.readerFont,
                                    onParagraphClick = {
                                        if (isSelected) {
                                            viewModel.clearParagraphSelection()
                                        } else {
                                            viewModel.selectParagraph(pIdx, paragraph)
                                        }
                                    },
                                    onBookmarkClick = {
                                        viewModel.addBookmarkForCurrentSelection()
                                    },
                                    onQuotePosterClick = {
                                        viewModel.setQuotePosterDialogVisible(true)
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Chapter Navigation Bottom Bar
                        Row(
                            modifier = Modifier
                                .widthIn(max = 760.dp)
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { viewModel.navigateChapter(-1) },
                                enabled = uiState.currentChapterIndex > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = sysColors.surface,
                                    contentColor = sysColors.text,
                                    disabledContainerColor = sysColors.surface.copy(alpha = 0.4f),
                                    disabledContentColor = sysColors.textMuted.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.border),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("prev_chapter_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("قسمت قبلی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = { viewModel.navigateChapter(1) },
                                enabled = uiState.currentChapterIndex < uiState.chapters.lastIndex,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = sysColors.surface,
                                    contentColor = sysColors.text,
                                    disabledContainerColor = sysColors.surface.copy(alpha = 0.4f),
                                    disabledContentColor = sysColors.textMuted.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.border),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("next_chapter_button")
                            ) {
                                Text("قسمت بعدی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }

                        // Extra bottom padding for floating dock
                        Spacer(modifier = Modifier.height(100.dp))
                    }

                    // Floating Reader Dock (at bottom)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    ) {
                        ReaderDock(
                            visible = !uiState.isFocusMode,
                            readingPercentage = readingPercent,
                            isAutoScrolling = uiState.isAutoScrolling,
                            autoScrollSpeed = uiState.settings.autoScrollSpeed,
                            sysColors = sysColors,
                            onToggleAutoScroll = viewModel::toggleAutoScroll,
                            onAdjustAutoScrollSpeed = viewModel::adjustAutoScrollSpeed,
                            onOpenReaderSettings = { viewModel.setReaderSettingsDialogVisible(true) },
                            onOpenBookmarks = { viewModel.setBookmarksDialogVisible(true) },
                            onToggleFocusMode = viewModel::toggleFocusMode
                        )
                    }

                    // Floating Exit Focus Mode Button (when focus mode is active)
                    if (uiState.isFocusMode) {
                        Button(
                            onClick = viewModel::toggleFocusMode,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .testTag("exit_focus_mode_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = sysColors.surfaceGlass),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.border)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = sysColors.text, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("خروج از تمرکز", color = sysColors.text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Live Toast Notification / Floating Achievement Unlock Banner
                    AnimatedVisibility(
                        visible = uiState.toastMessage != null,
                        enter = fadeIn() + slideInVertically { -it },
                        exit = fadeOut() + slideOutVertically { -it },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 20.dp)
                    ) {
                        uiState.toastMessage?.let { msg ->
                            val isDark = sysColors.isDark
                            val bannerBg = if (isDark) Color(0xFF1E293B).copy(alpha = 0.96f) else Color(0xFFFFFFFF).copy(alpha = 0.98f)
                            val bannerBorder = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                            val titleColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                            val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

                            Surface(
                                color = bannerBg,
                                shape = RoundedCornerShape(26.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, bannerBorder),
                                shadowElevation = 12.dp
                            ) {
                                val isBadgeUnlock = msg.contains("نشان") || msg.contains("بازگشایی") || msg.contains("🏆")
                                if (isBadgeUnlock) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF59E0B).copy(alpha = if (isDark) 0.25f else 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("🏆", fontSize = 16.sp)
                                            }
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = "بازگشایی دستاورد جدید",
                                                color = titleColor,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = msg.replace("🏆 ", ""),
                                                color = subtitleColor,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = msg,
                                            color = titleColor,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (uiState.showSystemSettingsDialog) {
            SystemSettingsDialog(
                currentSettings = uiState.settings,
                sysColors = sysColors,
                unlockedBadgesCount = uiState.readingStats.unlockedBadges.size,
                onSettingsChanged = viewModel::updateSettings,
                onShowToast = viewModel::showToast,
                onDismiss = { viewModel.setSystemSettingsDialogVisible(false) }
            )
        }

        uiState.newlyUnlockedBadge?.let { badge ->
            BadgeCelebrationDialog(
                badge = badge,
                sysColors = sysColors,
                onDismiss = { viewModel.dismissBadgeCelebration() }
            )
        }

        if (uiState.showReaderSettingsDialog) {
            ReaderSettingsDialog(
                currentSettings = uiState.settings,
                sysColors = sysColors,
                onSettingsChanged = viewModel::updateSettings,
                onDismiss = { viewModel.setReaderSettingsDialogVisible(false) }
            )
        }

        if (uiState.showQuotePosterDialog) {
            QuotePosterDialog(
                quoteText = uiState.selectedParagraphText.ifEmpty {
                    "«وقتی ماه کامل می‌شود و سایه عقاب بر سر مار می‌افتد...»"
                },
                chapterTitle = currentChapter.title,
                sysColors = sysColors,
                onDismiss = { viewModel.setQuotePosterDialogVisible(false) },
                onShowToast = viewModel::showToast
            )
        }

        if (uiState.showPatchImportDialog) {
            PatchImportDialog(
                sysColors = sysColors,
                onImportChapter = viewModel::importChapterPatch,
                onDismiss = { viewModel.setPatchImportDialogVisible(false) },
                onShowToast = viewModel::showToast
            )
        }

        if (uiState.showBookmarksDialog) {
            BookmarksDialog(
                bookmarks = uiState.bookmarks,
                sysColors = sysColors,
                onSelectBookmark = { bm ->
                    val targetIdx = uiState.chapters.indexOfFirst { it.id == bm.chapterId }
                    if (targetIdx >= 0) {
                        viewModel.selectChapter(targetIdx)
                    }
                },
                onDeleteBookmark = viewModel::removeBookmark,
                onDismiss = { viewModel.setBookmarksDialogVisible(false) }
            )
        }
    }
}

@Composable
fun DrawerTableOfContents(
    chapters: List<Chapter>,
    patchedChapterIds: Set<Int>,
    currentChapterIndex: Int,
    searchQuery: String,
    sysColors: com.example.ui.theme.SystemThemeColors,
    onSearchChange: (String) -> Unit,
    onSelectChapter: (Int) -> Unit,
    onDeleteChapterPatch: (Int) -> Unit,
    onCloseDrawer: () -> Unit
) {
    var chapterToDelete by remember { mutableStateOf<Chapter?>(null) }

    val filteredChapters = remember(chapters, searchQuery) {
        if (searchQuery.isBlank()) {
            chapters.mapIndexed { idx, ch -> idx to ch }
        } else {
            chapters.mapIndexed { idx, ch -> idx to ch }
                .filter { it.second.title.contains(searchQuery.trim(), ignoreCase = true) }
        }
    }

    if (chapterToDelete != null) {
        val target = chapterToDelete!!
        AlertDialog(
            onDismissRequest = { chapterToDelete = null },
            title = {
                Text(
                    text = "حذف پچ قسمت ${persianNumber(target.id)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = sysColors.text
                )
            },
            text = {
                Text(
                    text = "آیا از حذف محتوای پچ‌شده‌ی قسمت «${target.title}» و قفل شدن مجدد این قسمت اطمینان دارید؟",
                    fontSize = 13.sp,
                    color = sysColors.textMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteChapterPatch(target.id)
                        chapterToDelete = null
                    }
                ) {
                    Text(
                        text = "حذف و قفل مجدد 🗑️",
                        color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { chapterToDelete = null }) {
                    Text(text = "انصراف", color = sysColors.textMuted)
                }
            },
            containerColor = sysColors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Drawer Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "فهرست فصل‌های رمان",
                    color = sysColors.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "اثر ${NovelRepository.NOVEL_AUTHOR}",
                    color = sysColors.textMuted,
                    fontSize = 11.sp
                )
            }

            Surface(
                color = sysColors.accent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.accent.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "${persianNumber(NovelRepository.TOTAL_CHAPTERS)} قسمت",
                    color = sysColors.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search in Chapters
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("drawer_search_field"),
            placeholder = {
                Text(text = "جستجوی فصل...", color = sysColors.textMuted, fontSize = 12.sp)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = sysColors.textMuted, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "پاک کردن", tint = sysColors.textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = sysColors.bg.copy(alpha = 0.5f),
                unfocusedContainerColor = sysColors.bg.copy(alpha = 0.5f),
                focusedBorderColor = sysColors.accent,
                unfocusedBorderColor = sysColors.border,
                focusedTextColor = sysColors.text,
                unfocusedTextColor = sysColors.text
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = sysColors.border, thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        // Chapters List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredChapters, key = { it.second.id }) { (idx, ch) ->
                val isSelected = idx == currentChapterIndex
                val isCustomPatched = patchedChapterIds.contains(ch.id)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectChapter(idx) }
                        .testTag("toc_chapter_item_${ch.id}"),
                    color = if (isSelected) sysColors.primary else sysColors.surface.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) sysColors.accent else Color.Transparent
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${persianNumber(ch.id)}.",
                                color = if (isSelected) Color.White else sysColors.textMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = ch.title,
                                color = if (isSelected) Color.White else sysColors.text,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isCustomPatched) {
                                IconButton(
                                    onClick = { chapterToDelete = ch },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("delete_patch_button_${ch.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف پچ قسمت",
                                        tint = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFFFF5252),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (ch.isLocked) {
                                Surface(
                                    color = Color(0x33000000),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB74D),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "به‌زودی",
                                            color = Color(0xFFFFB74D),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParagraphCard(
    paragraphText: String,
    index: Int,
    isSelected: Boolean,
    isBookmarked: Boolean,
    readerColors: com.example.ui.theme.ReaderCanvasColors,
    sysColors: com.example.ui.theme.SystemThemeColors,
    fontSizeSp: Float,
    lineHeightMultiplier: Float,
    readerFont: com.example.data.model.PersianFont,
    onParagraphClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onQuotePosterClick: () -> Unit
) {
    val fontFamily = NovelThemes.getFontFamily(readerFont)
    val calculatedLineHeight = (fontSizeSp * lineHeightMultiplier).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) readerColors.surface.copy(alpha = 0.85f)
                else if (isBookmarked) Color(0x1AFF9800)
                else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.5.dp else if (isBookmarked) 1.dp else 0.dp,
                color = if (isSelected) sysColors.accent else if (isBookmarked) Color(0xFFFF9800) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onParagraphClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("paragraph_card_$index")
    ) {
        if (isBookmarked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "نشانک‌گذاری شده",
                    color = Color(0xFFFF9800),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = paragraphText,
            color = readerColors.text,
            fontSize = fontSizeSp.sp,
            lineHeight = calculatedLineHeight,
            textAlign = TextAlign.Justify,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal
        )

        // Action Toolbar Popup when Paragraph is selected
        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = sysColors.border, thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = sysColors.surface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.border),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onBookmarkClick() }
                        .testTag("action_bookmark_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isBookmarked) "حذف نشانک" else "نشانک",
                            color = sysColors.text,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = sysColors.surface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, sysColors.border),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onQuotePosterClick() }
                        .testTag("action_quote_poster_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = sysColors.accent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "ساخت پوستر",
                            color = sysColors.text,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


