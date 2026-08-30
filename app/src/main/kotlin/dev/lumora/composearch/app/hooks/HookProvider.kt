/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.hooks

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.lumora.composearch.core.network.repositories.ArticleRepository
import javax.inject.Inject

/**
 * Single dependency provider for all hooks. A hook reaches it with
 * `hiltViewModel<HookProvider>()` and calls the repository it needs inside a
 * `useQuery`/`useMutation` fetcher — so screens stay ViewModel-free and there is no
 * per-repository plumbing at each call site.
 *
 * Add each area's repository here as it lands.
 */
@HiltViewModel
class HookProvider @Inject constructor(
    val articleRepository: ArticleRepository,
) : ViewModel()
