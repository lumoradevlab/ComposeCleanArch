/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point and the root of the Hilt dependency graph. `@HiltAndroidApp`
 * triggers Hilt's code generation and creates the `SingletonComponent` that owns
 * app-wide singletons — including the `QueryClient` cache, which `MainActivity` injects
 * and hands to Compose via `ProvideQueryClient`.
 */
@HiltAndroidApp
class ComposeCleanArchApplication : Application()
