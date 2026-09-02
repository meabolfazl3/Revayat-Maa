package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdateAlt
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NovelRepository
import com.example.data.model.Chapter
import com.example.notification.NovelNotificationHelper
import com.example.util.ReleaseSchedule
import com.example.ui.components.AboutUsDialog
import com.example.ui.components.AppLogo
import com.example.ui.components.BookmarksDialog
import com.example.ui.components.BookCoverArt
import com.example.ui.components.ContactUsDialog
import com.example.ui.components.PatchImportDialog
import com.example.ui.components.SystemSettingsDialog
import com.example.ui.components.UnlockCodeDialog
import com.example.ui.components.UnlockSuccessPopup
import com.example.ui.components.persianNumber
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ReaderViewModel,
    uiState: ReaderUiState,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sysColors = com.example.ui.theme.NovelThemes.getSystemColors(uiState.settings.systemTheme)

    // Back button handling: Close drawers/dialogs first; if clean on Home, exit with 1 click
    val isAnyDialogOrDrawerOpen = drawerState.isOpen ||
        uiState.showAboutUsDialog ||
        uiState.showContactUsDialog ||
        uiState.showSystemSettingsDialog ||
        uiState.showPatchImportDialog ||
        uiState.showBookmarksDialog ||
        uiState.showUnlockCodeDialog ||
        uiState.showUnlockSuccessPopup

    BackHandler {
        when {
            drawerState.isOpen -> {
                scope.launch { drawerState.close() }
            }
            uiState.showAboutUsDialog -> {
                viewModel.setAboutUsDialogVisible(false)
            }
            uiState.showContactUsDialog -> {
                viewModel.setContactUsDialogVisible(false)
            }
            uiState.showSystemSettingsDialog -> {
                viewModel.setSystemSettingsDialogVisible(false)
            }
            uiState.showPatchImportDialog -> {
                viewModel.setPatchImportDialogVisible(false)
            }
            uiState.showBookmarksDialog -> {
                viewModel.setBookmarksDialogVisible(false)
            }
            uiState.showUnlockCodeDialog -> {
                viewModel.setUnlockCodeDialogVisible(false)
            }
            uiState.showUnlockSuccessPopup -> {
                viewModel.setUnlockSuccessPopupVisible(false)
            }
            else -> {
                viewModel.navigateToShelf()
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .fillMaxHeight(),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    HomeDrawerContent(
                        isAllUnlocked = uiState.isAllUnlocked,
                        sysColors = sysColors,
                        onCloseDrawer = { scope.launch { drawerState.close() } },
                        onOpenUnlockCode = {
                            scope.launch { drawerState.close() }
                            viewModel.setUnlockCodeDialogVisible(true)
                        },
                        onOpenSettings = {
                            scope.launch { drawerState.close() }
                            viewModel.setSystemSettingsDialogVisible(true)
                        },
                        onOpenContactUs = {
                            scope.launch { drawerState.close() }
                            viewModel.setContactUsDialogVisible(true)
                        },
                        onOpenAboutUs = {
                            scope.launch { drawerState.close() }
                            viewModel.setAboutUsDialogVisible(true)
                        },
                        onOpenBookmarks = {
                            scope.launch { drawerState.close() }
                            viewModel.setBookmarksDialogVisible(true)
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AppLogo(
                                        modifier = Modifier.size(26.dp),
                                        shapeRadius = 7.dp,
                                        elevation = 1.dp
                                    )
                                    Text(
                                        text = "راز الماس",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "فصل اول | ۳۹ قسمت",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("home_menu_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "منوی اصلی",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { viewModel.navigateToShelf() },
                                modifier = Modifier.testTag("back_to_shelf_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "بازگشت به قفسه رمان‌ها",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            ) { innerPadding ->
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Featured Book Card (The Secret of Diamonds)
                    FeaturedBookCard(
                        chapters = uiState.chapters,
                        currentChapterIndex = uiState.currentChapterIndex,
                        sysColors = sysColors,
                        onStartReading = {
                            val targetIdx = if (uiState.chapters.getOrNull(uiState.currentChapterIndex)?.isLocked == false) {
                                uiState.currentChapterIndex
                            } else {
                                uiState.chapters.indexOfLast { !it.isLocked }.coerceAtLeast(0)
                            }
                            viewModel.navigateToReader(targetIdx)
                        }
                    )

                    // Chapters Section Header
                    val unlockedCount = uiState.chapters.count { !it.isLocked }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "فهرست قسمت‌های رمان (${persianNumber(NovelRepository.TOTAL_CHAPTERS)} قسمت)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${persianNumber(unlockedCount)} قسمت در دسترس",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Chapters Grid Showcase
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(480.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        userScrollEnabled = true
                    ) {
                        itemsIndexed(uiState.chapters) { idx, chapter ->
                            HomeChapterCard(
                                chapter = chapter,
                                isCurrent = idx == uiState.currentChapterIndex,
                                onClick = {
                                    if (chapter.isLocked) {
                                        val releaseInfo = ReleaseSchedule.getStatusBadgeText(chapter.id, chapter.isLocked)
                                        viewModel.showToast("🔒 قسمت ${chapter.id} («${chapter.title}») در موعد مقرر ($releaseInfo) بازگشایی می‌شود.")
                                    } else {
                                        viewModel.navigateToReader(idx)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Dialogs
        if (uiState.showAboutUsDialog) {
            AboutUsDialog(onDismiss = { viewModel.setAboutUsDialogVisible(false) })
        }

        if (uiState.showContactUsDialog) {
            ContactUsDialog(onDismiss = { viewModel.setContactUsDialogVisible(false) })
        }

        if (uiState.showSystemSettingsDialog) {
            SystemSettingsDialog(
                currentSettings = uiState.settings,
                sysColors = sysColors,
                onSettingsChanged = viewModel::updateSettings,
                onDismiss = { viewModel.setSystemSettingsDialogVisible(false) }
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
                    viewModel.setBookmarksDialogVisible(false)
                    viewModel.selectChapterById(bm.chapterId)
                    viewModel.navigateToReader()
                },
                onDeleteBookmark = viewModel::removeBookmark,
                onDismiss = { viewModel.setBookmarksDialogVisible(false) }
            )
        }

        if (uiState.showUnlockCodeDialog) {
            UnlockCodeDialog(
                sysColors = sysColors,
                isAlreadyUnlocked = uiState.isAllUnlocked,
                onSubmitCode = { code ->
                    viewModel.submitUnlockCode(code)
                },
                onResetLock = {
                    viewModel.resetUnlockedState()
                },
                onDismiss = { viewModel.setUnlockCodeDialogVisible(false) }
            )
        }

        if (uiState.showUnlockSuccessPopup) {
            UnlockSuccessPopup(
                sysColors = sysColors,
                onDismiss = { viewModel.setUnlockSuccessPopupVisible(false) }
            )
        }
    }
}

@Composable
fun HomeDrawerContent(
    isAllUnlocked: Boolean,
    sysColors: com.example.ui.theme.SystemThemeColors = com.example.ui.theme.NovelThemes.getSystemColors(com.example.data.model.SystemTheme.TELEGRAM_DARK),
    onCloseDrawer: () -> Unit,
    onOpenUnlockCode: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenContactUs: () -> Unit,
    onOpenAboutUs: () -> Unit,
    onOpenBookmarks: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Redesigned Glassmorphic Drawer Header
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = sysColors.surface.copy(alpha = 0.92f),
            border = BorderStroke(1.dp, sysColors.border),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                sysColors.primary.copy(alpha = 0.14f),
                                sysColors.accent.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppLogo(
                                modifier = Modifier.size(48.dp),
                                shapeRadius = 14.dp,
                                elevation = 3.dp
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "روایت ما",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = sysColors.text
                                )
                                Text(
                                    text = "توسعه‌دهنده: ابوالفضل پورنجف",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = sysColors.textMuted
                                )
                            }
                        }

                        // Modern Close Button
                        Surface(
                            shape = CircleShape,
                            color = sysColors.surface.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, sysColors.border),
                            modifier = Modifier.size(34.dp)
                        ) {
                            IconButton(
                                onClick = onCloseDrawer,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "بستن منو",
                                    tint = sysColors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Publisher & Status Tags Row (Harmonious Dual-Pill layout)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = sysColors.primary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, sysColors.primary.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(sysColors.primary)
                                )
                                Text(
                                    text = "مرجع داستان‌ها و رمان‌ها",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = sysColors.primary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "کانال رسمی روایت ما",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sysColors.isDark) Color(0xFFFBBF24) else Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Navigation Menu Items with Pill-shaped Icon Containers
        DrawerMenuItem(
            icon = Icons.Default.Key,
            title = if (isAllUnlocked) "قفل‌گشایی رمان (فعال شده)" else "قفل‌گشایی قسمت‌ها با کد",
            subtitle = if (isAllUnlocked) "تمامی بخش‌ها در دسترس هستند" else "وارد کردن رمز عبور اختصاصی",
            iconBg = if (isAllUnlocked) Color(0xFF10B981).copy(alpha = 0.15f) else sysColors.primary.copy(alpha = 0.12f),
            iconTint = if (isAllUnlocked) Color(0xFF10B981) else sysColors.primary,
            titleColor = sysColors.text,
            subtitleColor = sysColors.textMuted,
            onClick = onOpenUnlockCode
        )

        DrawerMenuItem(
            icon = Icons.Default.Settings,
            title = "تنظیمات برنامه و تم",
            subtitle = "شخصی‌سازی فونت، رنگ و پیمایش",
            iconBg = sysColors.primary.copy(alpha = 0.12f),
            iconTint = sysColors.primary,
            titleColor = sysColors.text,
            subtitleColor = sysColors.textMuted,
            onClick = onOpenSettings
        )

        DrawerMenuItem(
            icon = Icons.Default.Campaign,
            title = "ارتباط با ما و کانال ایتا",
            subtitle = "عضویت در کانال و ارسال نظرات",
            iconBg = Color(0xFFF59E0B).copy(alpha = 0.15f),
            iconTint = if (sysColors.isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
            titleColor = sysColors.text,
            subtitleColor = sysColors.textMuted,
            onClick = onOpenContactUs
        )

        DrawerMenuItem(
            icon = Icons.Default.Favorite,
            title = "درباره ما",
            subtitle = "آشنایی با تیم نگارش و هدف رمان",
            iconBg = Color(0xFFEC4899).copy(alpha = 0.15f),
            iconTint = Color(0xFFDB2777),
            titleColor = sysColors.text,
            subtitleColor = sysColors.textMuted,
            onClick = onOpenAboutUs
        )

        DrawerMenuItem(
            icon = Icons.Default.Bookmark,
            title = "نشان‌شده‌ها و یادداشت‌ها",
            subtitle = "مرور فرازهای برگزیده داستان",
            iconBg = Color(0xFF8B5CF6).copy(alpha = 0.15f),
            iconTint = Color(0xFF7C3AED),
            titleColor = sysColors.text,
            subtitleColor = sysColors.textMuted,
            onClick = onOpenBookmarks
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            color = sysColors.border,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Footer Version Badge
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = sysColors.surface.copy(alpha = 0.7f),
            border = BorderStroke(1.dp, sysColors.border),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "نسخه ۳.۰ | روایت ما",
                style = MaterialTheme.typography.labelSmall,
                color = sysColors.textMuted,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconBg: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    iconTint: Color = MaterialTheme.colorScheme.primary,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconBg,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = subtitleColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeaturedBookCard(
    chapters: List<Chapter>,
    currentChapterIndex: Int,
    sysColors: com.example.ui.theme.SystemThemeColors,
    onStartReading: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = sysColors.surface
        ),
        border = BorderStroke(1.dp, sysColors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = sysColors.text.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book Cover Artwork with soft rounded corners
                BookCoverArt(
                    modifier = Modifier
                        .width(102.dp)
                        .height(145.dp)
                        .shadow(6.dp, RoundedCornerShape(14.dp))
                )

                // Book Info Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = sysColors.primary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, sysColors.primary.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "🌟 رمان معاصر پرتعلیق",
                            style = MaterialTheme.typography.labelSmall,
                            color = sysColors.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "راز الماس",
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = sysColors.text
                    )

                    Text(
                        text = "نویسنده: ابوالفضل پورنجف",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sysColors.text.copy(alpha = 0.85f)
                    )

                    Text(
                        text = "ناشر: کانال روایت ما",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = sysColors.accent,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = sysColors.bg.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, sysColors.border)
                        ) {
                            Text(
                                text = "${persianNumber(NovelRepository.TOTAL_CHAPTERS)} قسمت",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = sysColors.text,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = sysColors.primary.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, sysColors.primary.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "انتشار روزانه ۱۶:۰۰",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = sysColors.primary,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // Synopsis Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = sysColors.bg.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, sysColors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "سرقت ناممکن الماس آبی «اشک کوروش» از موزه ملی هنر، تله‌ای برای پوشاندن یک غارت باستانی بزرگ‌تر بود. آریا پارسا، متخصص مرمت آثار که متهم اصلی شده، در جستجوی حقیقت به دل رمز و رازهای کویر و دخمه‌های ساسانی می‌زند...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    lineHeight = 22.sp,
                    color = sysColors.text.copy(alpha = 0.9f),
                    modifier = Modifier.padding(14.dp)
                )
            }

            // Reading Progress Section
            val currentIdx = currentChapterIndex.coerceAtLeast(0)
            val totalChapters = chapters.size.coerceAtLeast(1)
            val progressFraction = ((currentIdx + 1).toFloat() / totalChapters.toFloat()).coerceIn(0.02f, 1f)
            val progressPercent = (progressFraction * 100).toInt()

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = sysColors.bg.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, sysColors.border.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "میزان پیشرفت مطالعه: قسمت ${persianNumber(currentIdx + 1)} از ${persianNumber(totalChapters)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sysColors.text
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${persianNumber(progressPercent)}٪",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Progress Track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(sysColors.border.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFFFFD700))
                                    )
                                )
                        )
                    }
                }
            }

            Button(
                onClick = onStartReading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = sysColors.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_reading_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                val targetChapter = chapters.getOrNull(currentChapterIndex)
                val readLabel = if (targetChapter != null && !targetChapter.isLocked) {
                    "ادامه مطالعه (قسمت ${persianNumber(targetChapter.id)})"
                } else {
                    val lastUnlocked = chapters.lastOrNull { !it.isLocked }
                    if (lastUnlocked != null) "ادامه مطالعه (قسمت ${persianNumber(lastUnlocked.id)})" else "شروع مطالعه رمان"
                }
                Text(
                    text = readLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun HomeChapterCard(
    chapter: Chapter,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            } else if (chapter.isLocked) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قسمت ${persianNumber(chapter.id)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (chapter.isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = if (chapter.isLocked) Icons.Default.Lock else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (chapter.isLocked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = chapter.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            val statusBadge = ReleaseSchedule.getStatusBadgeText(chapter.id, chapter.isLocked)
            Text(
                text = statusBadge,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = if (chapter.isLocked) Color(0xFFF59E0B) else Color(0xFF10B981)
            )
        }
    }
}
