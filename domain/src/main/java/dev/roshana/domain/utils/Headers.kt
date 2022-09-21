package dev.roshana.domain.utils

import java.util.*

/** PariSa;
coding and smoking ;)
 **/

val globalHeaders by lazy {
    mapOf(
        Pair("X-CHERAGH-CLIENT", "Android"),
        Pair("Accept-Language", Locale.getDefault().country)
    )
}


