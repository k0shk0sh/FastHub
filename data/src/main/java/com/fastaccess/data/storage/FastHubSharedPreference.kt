package com.fastaccess.data.storage

import android.app.Application
import android.preference.PreferenceManager
import com.fastaccess.data.BuildConfig
import com.securepreferences.SecurePreferences
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class FastHubSharedPreference @Inject constructor(app: Application) {

    private val preference = if (BuildConfig.DEBUG) {
        PreferenceManager.getDefaultSharedPreferences(app)
    } else {
        SecurePreferences(app, BuildConfig.APPLICATION_ID, "fasthub_preference_file")
    }

    private val editor = preference.edit()

    fun <T> set(key: String, value: T? = null) {
        value?.let {
            when (it) {
                is String -> {
                    when {
                        it.toBooleanOrNull() != null -> putBoolean(key, it.toBoolean())
                        it.toIntOrNull() != null -> putInt(key, it.toInt())
                        it.toFloatOrNull() != null -> putFloat(key, it.toFloat())
                        it.toLongOrNull() != null -> putLong(key, it.toLong())
                        else -> putString(key, it)
                    }
                }
                is Boolean -> putBoolean(key, it)
                is Int -> putInt(key, it)
                is Float -> putFloat(key, it)
                is Long -> putLong(key, it)
                else -> {
                    throw IllegalArgumentException("the type $it is not support")
                }
            }
        } ?: clearValue(key)
    }

    fun getString(key: String, default: String? = null): String? = kotlin.runCatching { preference.getString(key, default) }.getOrDefault(default)
    fun getInt(key: String, default: Int = 0): Int = kotlin.runCatching { preference.getInt(key, default) }.getOrDefault(default)
    fun getFloat(key: String, default: Float = 0f): Float = kotlin.runCatching { preference.getFloat(key, default) }.getOrDefault(default)
    fun getLong(key: String, default: Long = 0L): Long = kotlin.runCatching { preference.getLong(key, default) }.getOrDefault(default)
    fun getBoolean(key: String, default: Boolean = false): Boolean = kotlin.runCatching { preference.getBoolean(key, default) }.getOrDefault(default)

    fun getAll() = preference.all
    fun clearValue(key: String) = editor.remove(key).apply()
    fun clear() = editor.clear().apply()

    private fun putBoolean(key: String, value: Boolean) = editor.putBoolean(key, value).commit()
    private fun putInt(key: String, value: Int) = editor.putInt(key, value).commit()
    private fun putLong(key: String, value: Long) = editor.putLong(key, value).commit()
    private fun putFloat(key: String, value: Float) = editor.putFloat(key, value).commit()
    private fun putString(key: String, value: String) = editor.putString(key, value).commit()

    private fun String.toBooleanOrNull(): Boolean? = when {
        this.equals("true", true) -> true
        this.equals("false", true) -> false
        else -> null
    }
}