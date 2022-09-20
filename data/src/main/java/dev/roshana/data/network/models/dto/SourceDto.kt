package dev.roshana.data.network.models.dto

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceDto(
    @Keep val id: String? = null,
    @Keep val name: String? = null,
) : Parcelable
