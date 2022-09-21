package dev.roshana.domain.models.dialogs

/** PariSa;
coding and smoking ;)
 **/

data class Item(
    val id: Int,
    val title: String,
    val subtitle: String,
    val imageId: Int,
    val source: String = "demo source"
)