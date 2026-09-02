package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NovelRepository
import com.example.notification.NovelNotificationHelper
import com.example.ui.components.AboutUsDialog
import com.example.ui.components.AppLogo
import com.example.ui.components.BookCoverArt
import com.example.ui.components.BookmarksDialog
import com.example.ui.components.ContactUsDialog
import com.example.ui.components.PatchImportDialog
import com.example.ui.components.SystemSettingsDialog
import com.example.ui.components.UnlockCodeDialog
import com.example.ui.components.UnlockSuccessPopup
import com.example.ui.components.persianNumber
import com.example.ui.theme.NovelThemes
import com.example.ui.theme.SystemThemeColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(
    viewModel: ReaderViewModel,
    uiState: ReaderUiState,
    navigateToBookDetails: () -> Unit = { viewModel.navigateToBookDetails() },
    onBookClick: () -> Unit = navigateToBookDetails,
    onQuickRead: (Int) -> Unit = { chapterIdx -> viewModel.navigateToReader(chapterIdx) },
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sysColors = NovelThemes.getSystemColors(uiState.settings.systemTheme)

    val isAnyDialogOrDrawerOpen = drawerState.isOpen ||
        uiState.showAboutUsDialog ||
        uiState.showContactUsDialog ||
        uiState.showSystemSettingsDialog ||
        uiState.showPatchImportDialog ||
        uiState.showBookmarksDialog ||
        uiState.showUnlockCodeDialog ||
        uiState.showUnlockSuccessPopup

    BackHandler(enabled = isAnyDialogOrDrawerOpen) {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            uiState.showAboutUsDialog -> viewModel.setAboutUsDialogVisible(false)
            uiState.showContactUsDialog -> viewModel.setContactUsDialogVisible(false)
            uiState.showSystemSettingsDialog -> viewModel.setSystemSettingsDialogVisible(false)
            uiState.showPatchImportDialog -> viewModel.setPatchImportDialogVisible(false)
            uiState.showBookmarksDialog -> viewModel.setBookmarksDialogVisible(false)
            uiState.showUnlockCodeDialog -> viewModel.setUnlockCodeDialogVisible(false)
            uiState.showUnlockSuccessPopup -> viewModel.setUnlockSuccessPopupVisible(false)
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
                containerColor = sysColors.bg,
                topBar = {
                    // Glass Top Bar
                    Surface(
                        color = sysColors.surface.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, sysColors.border),
                        modifier = Modifier.shadow(4.dp)
                    ) {
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
                                            modifier = Modifier.size(28.dp),
                                            shapeRadius = 8.dp,
                                            elevation = 2.dp
                                        )
                                        Text(
                                            text = "روایت ما",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = sysColors.text
                                        )
                                    }
                                    Text(
                                        text = "مرجع داستان‌ها و رمان‌های پرکشش",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        color = sysColors.textMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("shelf_menu_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "منوی اصلی",
                                        tint = sysColors.text
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        val latestUnlocked = uiState.chapters.lastOrNull { !it.isLocked } ?: uiState.chapters.firstOrNull()
                                        if (latestUnlocked != null) {
                                            NovelNotificationHelper.notifyChapterUnlocked(
                                                context,
                                                latestUnlocked.id,
                                                latestUnlocked.title,
                                                forceNotify = true
                                            )
                                            viewModel.showToast("🔔 اعلان قسمت ${persianNumber(latestUnlocked.id)} («${latestUnlocked.title}») ارسال شد!")
                                        }
                                    },
                                    modifier = Modifier.testTag("shelf_notif_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "اعلان‌ها",
                                        tint = sysColors.textMuted
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://eitaa.com/revayate_maa"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.testTag("shelf_eitaa_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = "کانال ایتا",
                                        tint = sysColors.textMuted
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = innerPadding.calculateTopPadding() + 12.dp,
                            bottom = innerPadding.calculateBottomPadding() + 24.dp
                        )
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Banner / Title of Shelf
                    ShelfHeaderSection(sysColors = sysColors)

                    // Active Novel: "راز الماس" (Half-width book card)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        ActiveNovelShelfCard(
                            uiState = uiState,
                            sysColors = sysColors,
                            onClick = {
                                onBookClick()
                            },
                            onQuickRead = {
                                val targetIdx = if (uiState.chapters.getOrNull(uiState.currentChapterIndex)?.isLocked == false) {
                                    uiState.currentChapterIndex
                                } else {
                                    uiState.chapters.indexOfLast { !it.isLocked }.coerceAtLeast(0)
                                }
                                onQuickRead(targetIdx)
                            },
                            modifier = Modifier.width(195.dp)
                        )
                    }
                }
            }
        }

        // Shelf dialogs
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
                onSubmitCode = { code -> viewModel.submitUnlockCode(code) },
                onResetLock = { viewModel.resetUnlockedState() },
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
private fun ShelfHeaderSection(
    sysColors: SystemThemeColors
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = sysColors.surface.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, sysColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "📚 ویترین رمان‌ها",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = sysColors.text
                )
                Text(
                    text = "مجموعه آثار اختصاصی و داغ کانال",
                    style = MaterialTheme.typography.labelSmall,
                    color = sysColors.textMuted
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = sysColors.primary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, sysColors.primary.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "رمان در حال انتشار",
                    style = MaterialTheme.typography.labelSmall,
                    color = sysColors.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ActiveNovelShelfCard(
    uiState: ReaderUiState,
    sysColors: SystemThemeColors,
    onClick: () -> Unit,
    onQuickRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(),
        label = "card_scale"
    )

    val currentIdx = uiState.currentChapterIndex
    val totalChapters = NovelRepository.TOTAL_CHAPTERS
    val progressFraction = ((currentIdx + 1).toFloat() / totalChapters.toFloat()).coerceIn(0.02f, 1f)
    val progressPercent = (progressFraction * 100).toInt()

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = sysColors.surface.copy(alpha = 0.92f)
        ),
        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(10.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFF38BDF8).copy(alpha = 0.2f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("novel_card_raze_almas")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Book Cover with Controlled 240dp Height (Vertical Novel Ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                BookCoverArt(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(6.dp, RoundedCornerShape(14.dp))
                )

                // Top-Start Gold Badge: "فصل ۱" (Capsule style)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFF59E0B))
                            ),
                            RoundedCornerShape(8.dp)
                        )
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "فصل ۱",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E1B4B),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                    )
                }

                // Top-End Glass Badge: "۳۹ قسمت" (Capsule style)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x99000000),
                    border = BorderStroke(1.dp, Color(0x4DFFFFFF)),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "${persianNumber(totalChapters)} قسمت",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                    )
                }
            }

            // Novel Info Text Row
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "راز الماس",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = sysColors.text
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = sysColors.primary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, sysColors.primary.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "معمایی",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = sysColors.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "نویسنده: ابوالفضل پورنجف",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = sysColors.textMuted
                )
            }

            // Reading Progress Section
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قسمت ${persianNumber(currentIdx + 1)} از ${persianNumber(totalChapters)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.5.sp,
                        color = sysColors.textMuted
                    )
                    Text(
                        text = "${persianNumber(progressPercent)}٪",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }

                // Progress track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(sysColors.border)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFF59E0B), Color(0xFFFFD700))
                                )
                            )
                    )
                }
            }

            // Single Full-Width Action Button: "ادامه مطالعه" (or "شروع مطالعه")
            Button(
                onClick = onQuickRead,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = sysColors.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("shelf_quick_read_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (currentIdx == 0 && progressPercent < 5) "شروع مطالعه" else "ادامه مطالعه",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
