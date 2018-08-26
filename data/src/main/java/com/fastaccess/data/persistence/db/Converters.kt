package com.fastaccess.data.persistence.db

import androidx.room.TypeConverter
import com.fastaccess.data.persistence.models.*
import com.fastaccess.domain.response.enums.EventsType
import com.google.gson.Gson
import java.util.*

/**
 * Created by Kosh on 11.05.18.
 */


private fun Any.toJson() = Gson().toJson(this)

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
    @TypeConverter fun fromDbToValue(data: String? = null): PayloadModel? = data?.let { Gson().fromJson(it, PayloadModel::class.java) }
}

class FeedRepoConverter {
    @TypeConverter fun toDbValue(data: RepositoryModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): RepositoryModel? = data?.let { Gson().fromJson(it, RepositoryModel::class.java) }
}

class UserCountConverter {
    @TypeConverter fun toDbValue(data: UserCountModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(data: String? = null): UserCountModel? = data?.let { Gson().fromJson(it, UserCountModel::class.java) }
}

class UserOrganizationConverter {
    @TypeConverter fun toDbValue(data: UserOrganizationModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(
            data: String? = null): UserOrganizationModel? = data?.let { Gson().fromJson(it, UserOrganizationModel::class.java) }
}

class UserPinnedReposModelConverter {
    @TypeConverter fun toDbValue(data: UserPinnedReposModel? = null): String? = data?.toJson()
    @TypeConverter fun fromDbToValue(
            data: String? = null): UserPinnedReposModel? = data?.let { Gson().fromJson(it, UserPinnedReposModel::class.java) }
}