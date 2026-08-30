/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import javax.inject.Inject

/**
 * Turns ANY error body into a [ResponseError] — and **never throws**. This is what
 * makes "parse whatever the server returns" true: it walks the common message/
 * error field names across shapes, pulls out validation maps, digs one level into
 * nested `error` objects, and falls back to the raw text (then to a code-based
 * default) so an unexpected or malformed body still yields a usable error.
 *
 * Server error bodies are dynamic, so this is intentionally shape-agnostic. When a
 * new backend shape shows up, add its key to [MESSAGE_KEYS] (or a branch in
 * [fieldErrorsOf]) — that one edit is the entire maintenance cost.
 */
class ResponseErrorParser @Inject constructor() {

    fun parse(code: Int, rawBody: String?): ResponseError {
        if (rawBody.isNullOrBlank()) {
            return ResponseError(defaultMessage(code), code)
        }

        // Not valid JSON (plain text, HTML from a proxy, …) → still surface the text.
        val obj = runCatching { JsonParser.parseString(rawBody) }.getOrNull() as? JsonObject
            ?: return ResponseError(rawBody.trim().take(MAX_RAW_LEN), code, raw = rawBody)

        val fieldErrors = obj.fieldErrorsOf()
        // A validation error carries the specific, user-facing text per field (some
        // APIs nest it under `result`, e.g. `result.email`) — that is far more
        // useful than the generic envelope message, so prefer it when present.
        val message = fieldErrors.values.firstOrNull()?.firstOrNull()
            ?: MESSAGE_KEYS.firstNotNullOfOrNull { obj.stringAt(it) }
            ?: obj.nestedMessage()
            ?: defaultMessage(code)
        val serverCode = obj.intAt("code") ?: obj.intAt("status") ?: code

        return ResponseError(
            message = message,
            code = serverCode,
            fieldErrors = fieldErrors,
            raw = rawBody,
        )
    }

    private fun JsonObject.stringAt(key: String): String? =
        (get(key) as? JsonPrimitive)?.takeIf { it.isString }?.asString?.takeIf { it.isNotBlank() }

    private fun JsonObject.intAt(key: String): Int? =
        (get(key) as? JsonPrimitive)?.takeIf { it.isNumber }?.asInt

    /** `{ "error": { "message": "…" } }` style. */
    private fun JsonObject.nestedMessage(): String? {
        val nested = (get("error") as? JsonObject) ?: (get("data") as? JsonObject) ?: return null
        return MESSAGE_KEYS.firstNotNullOfOrNull { nested.stringAt(it) }
    }

    /**
     * Reads per-field validation messages. Handles both the `{ "errors": { "field":
     * ["msg", …] } }` shape and the `{ "result": { "field": "msg" } }` shape,
     * where each value is a single string or a list of strings.
     */
    private fun JsonObject.fieldErrorsOf(): Map<String, List<String>> {
        val errors = (get("errors") as? JsonObject)
            ?: (get("result") as? JsonObject)
            ?: return emptyMap()
        return errors.entrySet().associate { (field, value) ->
            field to when {
                value is JsonArray -> value.mapNotNull { (it as? JsonPrimitive)?.asString }
                value is JsonPrimitive -> listOf(value.asString)
                else -> emptyList()
            }
        }.filterValues { it.isNotEmpty() }
    }

    private fun defaultMessage(code: Int): String = when (code) {
        400 -> "Bad request"
        401 -> "Session expired — please sign in again"
        403 -> "You don't have permission to do that"
        404 -> "Not found"
        408 -> "Request timed out"
        409 -> "Conflict — please try again"
        422 -> "Some of the information is invalid"
        429 -> "Too many requests — slow down"
        in 500..599 -> "Server error — please try again later"
        -1 -> "Network error"
        else -> "Something went wrong"
    }

    private companion object {
        val MESSAGE_KEYS = listOf("message", "error", "detail", "title", "msg", "error_description", "reason")
        const val MAX_RAW_LEN = 300
    }
}
