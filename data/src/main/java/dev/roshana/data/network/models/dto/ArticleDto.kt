package dev.roshana.data.network.models.dto

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "article")
data class ArticleDto(
    @PrimaryKey(autoGenerate = true)
    @Keep
    val localId: Long = 0,
    @Keep
    var author: String? = null,
    @Keep
    var title: String? = null,
    @Keep
    var description: String? = null,
    @Keep
    var url: String? = null,
    @Keep
    var urlToImage: String? = null,
    @Keep
    var publishedAt: String? = null,
    @Keep
    var content: String? = null,
    @Keep
    var source: SourceDto? = null
) : Parcelable
