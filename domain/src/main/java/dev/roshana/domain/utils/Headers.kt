package dev.roshana.domain.utils

import java.util.*

val globalHeaders by lazy {
    mapOf(
        Pair("X-CHERAGH-CLIENT", "Android"),
        Pair("Accept-Language", Locale.getDefault().country)
    )
}


