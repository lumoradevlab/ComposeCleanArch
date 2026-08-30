/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.query

import kotlinx.coroutines.flow.Flow

/**
 * Optional Room/disk backing for a query — the "persistent tier".
 *
 * When present, Room is the single source of truth: the query streams its value
 * from [reader], and every successful network fetch is persisted via [writer]
 * (which makes [reader] emit the new value). Offline reads and a process-wide
 * shared cache come for free. Without a source of truth a query is memory-only
 * and its cache is lost on process death.
 *
 * This is the hand-rolled replacement for Store5's `SourceOfTruth` — same idea,
 * no third-party dependency. Wire one up with [useCachedQuery].
 */
class SourceOfTruth<T>(
    val reader: () -> Flow<T?>,
    val writer: suspend (T) -> Unit,
)
