/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.navigation

import kotlinx.serialization.Serializable

/* ============================================================================
 *  Navigation routes.
 *
 *  Type-safe Navigation-Compose routes (nav 2.8+): each destination is a
 *  @Serializable type, navigated to by value (`navController.navigate(ArticlesRoute)`)
 *  instead of by a stringly-typed path — so a typo is a compile error and arguments
 *  are real types rather than parsed strings.
 *
 *  These live in :app because :app is the only thing that builds the NavHost. If real
 *  feature modules ever arrive, each feature owns ITS OWN route + a
 *  `NavGraphBuilder.<name>Screen(...)` builder, and :app wires cross-feature jumps by
 *  passing lambdas down (`onOpenArticle = { navController.navigate(...) }`) — so no
 *  feature depends on another. Only extract these into a shared :core:navigation
 *  module if deep-linking forces it; until then that module is pure overhead.
 * ========================================================================== */

/**
 * Marker for the bottom-bar destinations. Lets the app shell type its tab table as
 * `TopLevelRoute` rather than `Any`, making "is this a top-level screen?" a
 * compile-time question.
 */
sealed interface TopLevelRoute

/** The headlines list — the app's entry point and first tab. */
@Serializable
data object ArticlesRoute : TopLevelRoute

/** Settings (theme switcher lives here). */
@Serializable
data object SettingsRoute : TopLevelRoute

/** Article detail, pushed on top of a tab. [url] doubles as the article's identity. */
@Serializable
data class ArticleDetailRoute(val url: String)
