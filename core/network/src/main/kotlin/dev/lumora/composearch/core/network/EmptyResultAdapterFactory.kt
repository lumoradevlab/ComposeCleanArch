/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * Field adapter for [ApiResponse.result] that reads a backend "empty" `[]` as null.
 *
 * Some backends (PHP/Laravel especially) serialise an *empty* associative `result` as a
 * JSON array `[]` and a populated one as an object `{}`. Gson's reflective adapter for the
 * declared result type then throws "Expected BEGIN_OBJECT but was BEGIN_ARRAY" on the `[]`,
 * turning a genuine 200 into a UI error — e.g. a success body of
 * `{"success":true,...,"result":[]}` on any endpoint whose result is an object or `Unit`.
 *
 * Applied via [@JsonAdapter][com.google.gson.annotations.JsonAdapter] on the `result` field,
 * so Gson hands us the *resolved* result type per endpoint. Only a non-collection result
 * coerces `[]` to null; a genuine `List`/array/`Map` result keeps `[]` as its valid empty
 * value by delegating to the normal adapter. Everything that isn't an empty array (an object,
 * null, a real list) is parsed by the delegate unchanged.
 */
class EmptyResultAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, type)
        // A collection/array/map result legitimately arrives as `[]` (empty) — don't touch it.
        val collectionLike = Collection::class.java.isAssignableFrom(type.rawType) ||
            Map::class.java.isAssignableFrom(type.rawType) ||
            type.rawType.isArray

        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T?) = delegate.write(out, value)

            override fun read(reader: JsonReader): T? = when {
                reader.peek() == JsonToken.NULL -> {
                    reader.nextNull()
                    null
                }
                // The backend's empty-object sentinel for a value we model as an object.
                !collectionLike && reader.peek() == JsonToken.BEGIN_ARRAY -> {
                    reader.skipValue()
                    null
                }
                else -> delegate.read(reader)
            }
        }
    }
}
