/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network

import dev.lumora.composearch.core.common.result.AppError
import dev.lumora.composearch.core.common.result.HttpStatusException
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the one-way street from a thrown failure to the single UI-facing error type:
 * a screen should never have to inspect an HTTP code itself.
 */
class AppErrorMappingTest {

    private fun errorFor(code: Int): AppError =
        AppError.from(ResponseException(ResponseError("boom", code)))

    @Test
    fun `transport failures map to Network`() {
        assertTrue(AppError.from(IOException("offline")) is AppError.Network)
        assertTrue(errorFor(HttpStatusException.NETWORK_ERROR_CODE) is AppError.Network)
    }

    @Test
    fun `auth, validation, rate limit and server codes each get their own type`() {
        assertTrue(errorFor(401) is AppError.Unauthorized)
        assertTrue(errorFor(403) is AppError.Unauthorized)
        assertTrue(errorFor(422) is AppError.Validation)
        assertTrue(errorFor(429) is AppError.RateLimited)
        assertTrue(errorFor(503) is AppError.Server)
    }

    @Test
    fun `an unmapped code falls through to Unknown and keeps its message`() {
        val error = errorFor(418)
        assertTrue(error is AppError.Unknown)
        assertEquals("boom", error.message)
    }
}
