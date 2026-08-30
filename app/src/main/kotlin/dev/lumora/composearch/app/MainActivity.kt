/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.lumora.composearch.app.ui.ComposeCleanArchApp
import dev.lumora.composearch.core.datastore.ThemeMode
import dev.lumora.composearch.core.datastore.ThemeStore
import dev.lumora.composearch.core.designsystem.theme.AppTheme
import dev.lumora.composearch.core.query.ProvideQueryClient
import dev.lumora.composearch.core.query.QueryClient
import dev.lumora.composearch.core.ui.theme.ProvideTheme
import dev.lumora.composearch.core.ui.theme.useThemeMode
import javax.inject.Inject

/**
 * The single Activity. Its only jobs are to supply the app-wide singletons to Compose
 * and resolve the theme — everything else is composables and navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // The single, app-wide cache, built by Hilt's SingletonComponent.
    @Inject lateinit var queryClient: QueryClient

    // Persisted theme preference (client state), read via the useThemeMode() hook.
    @Inject lateinit var themeStore: ThemeStore

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate() — it swaps the launch theme for the
        // post-splash theme, so calling it later would leave the branded window in place.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Make the cache and theme store reachable from every hook below, without
            // threading them through each Composable. Wrapped once, here at the root.
            ProvideQueryClient(queryClient) {
                ProvideTheme(themeStore) {
                    // The persisted choice → resolved dark/light for AppTheme. Because every
                    // component reads AppTheme.colors, this single flag re-themes the whole app.
                    val theme = useThemeMode()
                    val darkTheme = when (theme.mode) {
                        ThemeMode.LIGHT -> false
                        ThemeMode.DARK -> true
                        ThemeMode.SYSTEM -> isSystemInDarkTheme()
                    }
                    AppTheme(darkTheme = darkTheme) {
                        // Keep the status/nav-bar icon contrast in sync with the theme — as a
                        // side effect, not during composition.
                        val view = LocalView.current
                        SideEffect {
                            val window = (view.context as Activity).window
                            WindowCompat.getInsetsController(window, view)
                                .isAppearanceLightStatusBars = !darkTheme
                        }
                        ComposeCleanArchApp()
                    }
                }
            }
        }
    }
}
