package com.fastaccess.provider.markdown

import java.util.*

/**
 * Created by kosh on 11/08/2017.
 */
class CachedComments private constructor() {

    val map = WeakHashMap<String, CharSequence>()

    fun put(repo: String?, login: String?, number: Any?, comment: CharSequence) {
        map.put("$repo/$login/$number", comment)
    }

    fun get(repo: String?, login: String?, number: Any?): CharSequence? {
        return map["$repo/$login/$number"]
    }

    fun clear() = map.clear()

    private object Holder {
        val INSTANCE = CachedComments()
    }

    companion object {
        val instance: CachedComments by lazy { Holder.INSTANCE }
    }
}