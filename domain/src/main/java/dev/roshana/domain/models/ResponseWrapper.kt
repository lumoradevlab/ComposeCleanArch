package dev.roshana.domain.models

import androidx.annotation.Keep

/** PariSa;
coding and smoking ;)
 **/

@Keep
class ResponseWrapper<T> {
    @Keep
    var status = ""

    @Keep
    var totalResults: String? = null

    @Keep
    var message: String? = null

    @Keep
    var articles: T? = null
}


