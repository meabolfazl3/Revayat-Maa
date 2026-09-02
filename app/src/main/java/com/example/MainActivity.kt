package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.notification.NovelNotificationHelper
import com.example.ui.HomeScreen
import com.example.ui.ReaderScreen
import com.example.ui.ReaderViewModel
import com.example.ui.ScreenMode
import com.example.ui.ShelfScreen
import com.example.ui.components.SplashScreen
import com.example.ui.theme.NovelThemes
import com.example.ui.theme.RazeAlmasTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: ReaderViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NovelNotificationHelper.scheduleAllUpcomingChapterAlarms(applicationContext)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        NovelNotificationHelper.createNotificationChannel(this)
        NovelNotificationHelper.scheduleAllUpcomingChapterAlarms(applicationContext)
        checkNotificationPermission()
        handleIntent(intent)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val sysColors = NovelThemes.getSystemColors(uiState.settings.systemTheme)
            var showSplash by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                viewModel.checkAndNotifyNewUnlockedChapters()
                delay(1800)
                showSplash = false
            }

            RazeAlmasTheme(
                systemTheme = uiState.settings.systemTheme,
                uiFont = uiState.settings.uiFont,
                uiScalePercent = uiState.settings.uiScalePercent
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = sysColors.bg
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AnimatedContent(
                            targetState = uiState.currentScreen,
                            transitionSpec = {
                                if (targetState.ordinal > initialState.ordinal) {
                                    (slideInHorizontally(tween(300)) { -it / 2 } + fadeIn(tween(280)))
                                        .togetherWith(slideOutHorizontally(tween(260)) { it / 2 } + fadeOut(tween(240)))
                                } else {
                                    (slideInHorizontally(tween(300)) { it / 2 } + fadeIn(tween(280)))
                                        .togetherWith(slideOutHorizontally(tween(260)) { -it / 2 } + fadeOut(tween(240)))
                                }
                            },
                            label = "app_view_transition"
                        ) { screen ->
                            when (screen) {
                                ScreenMode.SHELF -> ShelfScreen(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    navigateToBookDetails = { viewModel.navigateToBookDetails() },
                                    onBookClick = { viewModel.navigateToBookDetails() },
                                    onQuickRead = { idx -> viewModel.navigateToReader(idx) }
                                )
                                ScreenMode.BOOK_DETAILS -> HomeScreen(viewModel = viewModel, uiState = uiState)
                                ScreenMode.READER -> ReaderScreen(viewModel = viewModel)
                            }
                        }

                        // Seamless Splash Screen Overlay with Luxury Fade-out
                        AnimatedVisibility(
                            visible = showSplash,
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(450))
                        ) {
                            SplashScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshChapters()
        viewModel.checkAndNotifyNewUnlockedChapters()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val targetChId = intent?.getIntExtra(NovelNotificationHelper.EXTRA_CHAPTER_ID, -1) ?: -1
        if (targetChId > 0) {
            viewModel.selectChapterById(targetChId)
            viewModel.navigateToReader()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

