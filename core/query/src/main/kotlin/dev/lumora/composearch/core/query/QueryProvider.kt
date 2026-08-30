/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.query

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Compose-side handle on the singleton [QueryClient], mirroring React's
 * `<QueryClientProvider>`. Reading it without a provider is a programmer error
 * (the app root must wrap content in [ProvideQueryClient]), so the default throws
 * instead of silently doing nothing.
 */
val LocalQueryClient: ProvidableCompositionLocal<QueryClient> =
    staticCompositionLocalOf { error("QueryClient not provided. Wrap your app in ProvideQueryClient.") }

/** Wrap the app root once, supplying the Hilt-provided [queryClient]. */
@Composable
fun ProvideQueryClient(queryClient: QueryClient, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalQueryClient provides queryClient, content = content)
}
