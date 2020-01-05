package com.fastaccess.data.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import io.reactivex.Observable
import io.reactivex.Observable.fromPublisher
import io.reactivex.internal.subscriptions.BooleanSubscription
import java.io.InputStreamReader

/**
 * Created by Kosh on 23.01.19.
 */
data class LanguageColorsModel(val name: String,
                               val color: String? = null,
                               val url: String? = null) {

    private class LanguageSerializableMode(@SerializedName("color") var color: String? = null,
                                           @SerializedName("url") var url: String? = null)

    companion object {
        fun newInstance(gson: Gson, context: Context): Observable<List<LanguageColorsModel>> = fromPublisher { publisher ->
            val booleanSubscription = BooleanSubscription()
            publisher.onSubscribe(booleanSubscription)
            try {
                val list = arrayListOf<LanguageColorsModel>()
                val type = object : TypeToken<Map<String, LanguageSerializableMode>>() {}.type
                context.assets.open("languages.json").use { stream ->
                    JsonReader(InputStreamReader(stream)).use {
                        gson.fromJson<Map<String, LanguageSerializableMode>>(it, type)?.forEach { (key, value) ->
                            list.add(LanguageColorsModel(key, value.color, value.url))
                        }
                    }
                }
                list.sortByDescending {
                    it.name == "Java" || it.name == "Kotlin" || it.name == "JavaScript" || it.name == "Python" ||
                        it.name == "CSS" || it.name == "PHP" || it.name == "Ruby" || it.name == "C++" || it.name == "C" ||
                        it.name == "Go" || it.name == "Swift"
                }
                list.add(0, LanguageColorsModel("All"))
                publisher.onNext(list.distinctBy { it.name })
            } catch (e: Exception) {
                publisher.onError(e)
            }
            publisher.onComplete()
        }
    }
}