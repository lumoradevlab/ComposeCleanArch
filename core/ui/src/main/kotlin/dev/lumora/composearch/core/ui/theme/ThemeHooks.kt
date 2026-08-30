package dev.lumora.composearch.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.lumora.composearch.core.datastore.ThemeMode
import dev.lumora.composearch.core.datastore.ThemeStore
import kotlinx.coroutines.launch

/**
 * Compose handle on the singleton [ThemeStore], mirroring `LocalQueryClient`. Reading
 * it without a provider is a programmer error (the app root must wrap content in
 * [ProvideTheme]), so the default throws instead of silently doing nothing.
 */
val LocalThemeStore: ProvidableCompositionLocal<ThemeStore> =
    staticCompositionLocalOf { error("ThemeStore not provided. Wrap your app in ProvideTheme.") }

/** Wrap the app root once, supplying the Hilt-provided [store]. */
@Composable
fun ProvideTheme(store: ThemeStore, content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalThemeStore provides store, content = content)

/** What [useThemeMode] returns: the current mode plus a setter. Call it like a hook. */
data class ThemeResult(
    val mode: ThemeMode,
    val setMode: (ThemeMode) -> Unit,
)

/**
 * The theme hook — same shape as `useQuery` / `useMutation`, but backed by DataStore.
 *
 * Theme is local *client* state (a single persisted choice, observed continuously),
 * not server state, so it does NOT go through the QueryClient — it reads [ThemeStore]
 * via [LocalThemeStore] instead. Because every component reads `AppTheme.colors`,
 * feeding the returned [ThemeResult.mode] into a single `AppTheme(darkTheme = …)` at
 * the root re-themes the whole app; no global flag.
 *
 * ```
 * val theme = useThemeMode()
 * IconButton(onClick = { theme.setMode(ThemeMode.DARK) }) { … }
 * ```
 */
@Composable
fun useThemeMode(): ThemeResult {
    val store = LocalThemeStore.current
    val scope = rememberCoroutineScope()
    val mode by store.themeMode.collectAsStateWithLifecycle(ThemeMode.SYSTEM)
    return remember(mode) {
        ThemeResult(
            mode = mode,
            setMode = { newMode -> scope.launch { store.setThemeMode(newMode) } },
        )
    }
}
