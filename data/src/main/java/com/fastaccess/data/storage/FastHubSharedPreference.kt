package com.fastaccess.data.storage

import android.app.Application
import com.fastaccess.data.BuildConfig
import com.securepreferences.SecurePreferences
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class FastHubSharedPreference @Inject constructor(app: Application) {

    private val preference = SecurePreferences(app, BuildConfig.APPLICATION_ID, "fasthub_preference_file")
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

    fun getString(key: String, default: String? = null): String? = preference.getString(key, default)
    fun getInt(key: String, default: Int = 0): Int = preference.getInt(key, default)
    fun getFloat(key: String, default: Float = 0f): Float = preference.getFloat(key, default)
    fun getLong(key: String, default: Long = 0L): Long = preference.getLong(key, default)
    fun getBoolean(key: String, default: Boolean = false): Boolean = preference.getBoolean(key, default)

    fun getAll() = preference.all
    fun clearValue(key: String) = editor.remove(key).apply()
    fun clear() = editor.clear().apply()

    private fun putBoolean(key: String, value: Boolean) = editor.putBoolean(key, value).commit()
    private fun putInt(key: String, value: Int) = editor.putInt(key, value).commit()
    private fun putLong(key: String, value: Long) = editor.putLong(key, value).commit()
    private fun putFloat(key: String, value: Float) = editor.putFloat(key, value).commit()
    private fun putString(key: String, value: String) = editor.putString(key, value).commit()

    private fun String.toBooleanOrNull(): Boolean? = try {
        java.lang.Boolean.parseBoolean(this)
    } catch (e: Exception) {
        null
    }
}