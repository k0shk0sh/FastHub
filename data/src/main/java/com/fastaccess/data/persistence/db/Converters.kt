package com.fastaccess.data.persistence.db

import androidx.room.TypeConverter
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.ReactionGroupModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.persistence.models.PayloadModel
import com.fastaccess.data.persistence.models.RepositoryModel
import com.fastaccess.data.persistence.models.UserOrganizationModel
import com.fastaccess.data.persistence.models.UserPinnedReposModel
import com.fastaccess.domain.response.enums.EventsType
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Kosh on 11.05.18.
 */

private fun Any.toJson() = gson.toJson(this)

private fun <T> String?.fromJson(clazz: Class<T>) = kotlin.runCatching { gson.fromJson(this, clazz) }.getOrElse {
    gsonFallback.fromJson(this, clazz)
}

private fun <T> String?.fromJson(type: Type) = kotlin.runCatching { gson.fromJson<T>(this, type) }.getOrElse {
    gsonFallback.fromJson<T>(this, type)
}

private val gson = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
    .setDateFormat("yyyy-MM-dd HH:mm:ss")
    .disableHtmlEscaping()
    .setPrettyPrinting()
    .create()

private val gsonFallback = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
    .setDateFormat("MMM d, yyyy HH:mm:ss")
    .disableHtmlEscaping()
    .setPrettyPrinting()
    .create()

class DateConverter {
    @TypeConverter fun toDbValue(date: Date? = null): Long? = date?.time
    @TypeConverter fun fromDbToValue(date: Long? = 0): Date? = date?.let { Date(it) }
}

class EventTypesConverter {
    @TypeConverter fun toDbValue(data: EventsType? = null): String? = data?.name
    @TypeConverter fun fromDbToValue(data: String? = null): EventsType? = data?.let { EventsType.valueOf(it) }
}

class FeedPayloadConverter {
    @TypeConverter fun toDbValue(data: PayloadModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): PayloadModel? = data.fromJson(PayloadModel::class.java)
}

class FeedRepoConverter {
    @TypeConverter fun toDbValue(data: RepositoryModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): RepositoryModel? = data.fromJson(RepositoryModel::class.java)
}

class UserCountConverter {
    @TypeConverter fun toDbValue(data: CountModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): CountModel? = data.fromJson(CountModel::class.java)
}

class UserOrganizationConverter {
    @TypeConverter fun toDbValue(data: UserOrganizationModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(
        data: String? = null): UserOrganizationModel? = data.fromJson(UserOrganizationModel::class.java)
}

class UserPinnedReposModelConverter {
    @TypeConverter fun toDbValue(data: UserPinnedReposModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): UserPinnedReposModel? = data.fromJson(UserPinnedReposModel::class.java)
}

class ReactionGroupConverter {
    @TypeConverter fun toDbValue(data: List<ReactionGroupModel>? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): List<ReactionGroupModel>? = data.fromJson(object :
        TypeToken<List<ReactionGroupModel>>() {}.type)
}

class StringArrayConverter {
    @TypeConverter fun toDbValue(data: List<String>? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): List<String>? = data.fromJson(object : TypeToken<List<String>>() {}.type)
}

class LabelsConverter {
    @TypeConverter fun toDbValue(data: List<LabelModel>? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): List<LabelModel>? = data.fromJson(object : TypeToken<List<LabelModel>>() {}.type)
}

class ShortUserModelsConverter {
    @TypeConverter fun toDbValue(data: List<ShortUserModel>? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): List<ShortUserModel>? = data?.fromJson(object :
        TypeToken<List<ShortUserModel>>() {}.type)
}