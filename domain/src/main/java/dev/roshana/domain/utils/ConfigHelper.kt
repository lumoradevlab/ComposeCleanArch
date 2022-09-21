package  dev.roshana.domain.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import dev.roshana.domain.BuildConfig

/** PariSa;
coding and smoking ;)
 **/

fun isDebugMode(): Boolean {
    if (BuildConfig.DEBUG)
        return true
    return false
}


const val AUTH_CODE = "auth2"
private const val TEST_PREFERENCE_NAME = "testPreference"
lateinit var sharedPreferences: SharedPreferences

lateinit var testPreferences: SharedPreferences

private val authorization: String?
    get() = getStringPreference(AUTH_CODE)

fun initPreferenceUtils(context: Context) {
    sharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    testPreferences =
        context.getSharedPreferences(TEST_PREFERENCE_NAME, Context.MODE_PRIVATE)
}

fun getStringPreference(tag: String): String? {
    return sharedPreferences.getString(tag, null)
}

@SuppressLint("ApplySharedPref")
fun putStringPreference(tag: String, value: String) {
    sharedPreferences.edit().putString(tag, value).commit()
}

fun putIntTestPreference(tag: String, value: Int) {
    testPreferences.edit().putInt(tag, value).apply()
}

fun getIntTestPreference(tag: String, defaultValue: Int = -1): Int {
    return testPreferences.getInt(tag, defaultValue)
}


fun putLongTesPreference(tag: String, value: Long) {
    testPreferences.edit().putLong(tag, value).apply()
}

fun getLongTestPreference(tag: String): Long {
    return testPreferences.getLong(tag, -1L)
}

fun putLongPreferenceTagStartWith(tag: String, value: Long) {
    val editor = sharedPreferences.edit()
    for (key in sharedPreferences.all.keys)
        if (key.startsWith(tag))
            editor.putLong(key, value)
    editor.apply()
}

fun getLongPreference(tag: String): Long {
    return sharedPreferences.getLong(tag, -1L)
}

fun getIntPreference(tag: String): Int {
    return sharedPreferences.getInt(tag, -1)
}

fun putIntPreference(tag: String, value: Int) {
    sharedPreferences.edit().putInt(tag, value).apply()
}

fun getBooleanPreference(pref: String): Boolean {
    return sharedPreferences.getBoolean(pref, false)
}

fun putBooleanPreference(pref: String, value: Boolean) {
    sharedPreferences.edit().putBoolean(pref, value).apply()
}

fun saveAuthorization(authorization: String) {
    putStringPreference(
        AUTH_CODE,
        authorization
    )
}

fun getShownIqTests() = sharedPreferences.getStringSet("shownIqTests", mutableSetOf())
fun isShownIqTests(id: Long) =
    sharedPreferences.getStringSet("shownIqTests", mutableSetOf())?.contains(id.toString()) ?: false

fun putIqTestAsShown(id: Long) = getShownIqTests()
    ?.apply {
        add(id.toString())
        sharedPreferences.edit().putStringSet("shownIqTests", this).apply()
    }

fun removeIqTestAsShown(id: Long) = getShownIqTests()
    ?.apply {
        remove(id.toString())
        sharedPreferences.edit().putStringSet("shownIqTests", this).apply()
    }

fun getShownMBTITests() = sharedPreferences.getStringSet("shownMBTITests", mutableSetOf())
fun isShownMBTITests(id: Long) =
    sharedPreferences.getStringSet("shownMBTITests", mutableSetOf())?.contains(id.toString())
        ?: false

fun putMBTITestAsShown(id: Long) = getShownMBTITests()
    ?.apply {
        add(id.toString())
        sharedPreferences.edit().putStringSet("shownMBTITests", this).apply()
    }

fun removeMBTITestAsShown(id: Long) = getShownMBTITests()
    ?.apply {
        remove(id.toString())
        sharedPreferences.edit().putStringSet("shownMBTITests", this).apply()
    }

@Synchronized
fun getToken(): String {
    return if (authorization == null) "" else getStringPreference(
        AUTH_CODE
    ).toString()
}

@Synchronized
fun removeAuthorization() = sharedPreferences.edit().remove(
    AUTH_CODE
).apply()


fun isUserSignedIn() = getToken().isNotEmpty()

