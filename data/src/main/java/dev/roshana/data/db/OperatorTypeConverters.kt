package dev.roshana.data.db

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.roshana.data.network.models.dto.ArticleDto
import dev.roshana.data.network.models.dto.SourceDto

/** PariSa;
coding and smoking ;)
**/

object OperatorTypeConverters {

    @TypeConverter
    @JvmStatic
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").map {
                try {
                    it.toInt()
                } catch (ex: NumberFormatException) {
                    Log.e("OperatorTypeConverters", "Cannot convert ${ex.message} to number")
                    null
                }
            }
        }?.filterNotNull()
    }

    @TypeConverter
    @JvmStatic
    fun listIntToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }

    @TypeConverter
    @JvmStatic
    fun stringToMap(value: String): Map<String, String> =
        fromJson(value)

    @TypeConverter
    @JvmStatic
    fun mapToString(value: Map<String, String>?): String =
        toJson(value)

    @TypeConverter
    @JvmStatic
    fun stringToSource(value: String): SourceDto =
        fromJson(value)

    @TypeConverter
    @JvmStatic
    fun sourceToString(value: SourceDto): String = toJson(value)

    @TypeConverter
    @JvmStatic
    fun stringToArticle(value: String): ArticleDto =
        fromJson(value)

    @TypeConverter
    @JvmStatic
    fun articleToString(value: ArticleDto): String = toJson(value)


    @TypeConverter
    @JvmStatic
    fun stringToStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        return fromJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun stringListToString(items: List<String>?): String = toJson(items)

    inline fun <reified T> toJson(value: T): String {
        return if (value == null) "" else Gson().toJson(value)
    }

    inline fun <reified T> fromJson(value: String): T {
        return Gson().fromJson(value, object : TypeToken<T>() {}.type)
    }

}

