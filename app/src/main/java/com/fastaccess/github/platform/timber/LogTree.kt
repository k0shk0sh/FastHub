package com.fastaccess.github.platform.timber

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

class FabricTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
        Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
        Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)
        if (t == null) {
            Crashlytics.logException(Exception(message))
        } else {
            Crashlytics.logException(t)
        }
    }

    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}

class FastHubTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        val className = super.createStackElementTag(element)?.split("$")?.get(0)
        return "($className.kt:${element.lineNumber})#${element.methodName}"
    }
}