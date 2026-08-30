package dev.lumora.composearch.core.database

import androidx.room.TypeConverter

/**
 * Room type converters shared by every feature's database.
 *
 * This module owns only SHARED Room infrastructure. Each feature declares its own
 * `@Database`/`@Entity`/`@Dao` in its own module, so :core:database never depends on a
 * feature's tables — that is what keeps the persistent tier optional per feature.
 *
 * Wire these into a feature database with `@TypeConverters(CommonConverters::class)`.
 */
class CommonConverters {

    /** Stores a list of strings as a single delimited column. */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? = value?.joinToString(SEPARATOR)

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        value?.takeIf { it.isNotEmpty() }?.split(SEPARATOR).orEmpty()

    private companion object {
        // ASCII unit separator, so ordinary punctuation inside a value never splits a row.
        const val SEPARATOR = "\u001F"
    }
}
