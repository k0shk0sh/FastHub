package com.fastaccess.data.storage

import android.content.Context
import android.preference.PreferenceManager
import com.fastaccess.data.di.annotations.ForApplication
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class FastHubSharedPreference @Inject constructor(@ForApplication context: Context) {

    private val preference = PreferenceManager.getDefaultSharedPreferences(context)
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
            }
        } ?: clearValue(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, default: T? = null): T? {
        return preference.all[key] as T? ?: default
    }

    fun getAll() = preference.all
    fun clearValue(key: String) = editor.remove(key).apply()
    fun clear() = editor.clear().apply()

    private fun putBoolean(key: String, value: Boolean) = editor.putBoolean(key, value).apply()
    private fun putInt(key: String, value: Int) = editor.putInt(key, value).apply()
    private fun putLong(key: String, value: Long) = editor.putLong(key, value).apply()
    private fun putFloat(key: String, value: Float) = editor.putFloat(key, value).apply()
    private fun putString(key: String, value: String) = editor.putString(key, value).apply()

    private fun String.toBooleanOrNull(): Boolean? = try {
        java.lang.Boolean.parseBoolean(this)
    } catch (e: Exception) {
        null
    }
}