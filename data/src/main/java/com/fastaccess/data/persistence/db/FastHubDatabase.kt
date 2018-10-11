package com.fastaccess.data.persistence.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fastaccess.data.persistence.dao.*
import com.fastaccess.data.persistence.models.*

/**
 * Created by Kosh on 11.05.18.
 */

const val VERSION = 6
const val DATABASE_NAME = "FastHub-Room-DB"

@Database(version = VERSION, entities = [UserModel::class, MainIssuesPullsModel::class,
    NotificationModel::class, FeedModel::class, ProfileRepoModel::class], exportSchema = false)
@TypeConverters(DateConverter::class, EventTypesConverter::class,
        FeedPayloadConverter::class, FeedRepoConverter::class,
        UserCountConverter::class, UserOrganizationConverter::class,
        UserPinnedReposModelConverter::class)
abstract class FastHubDatabase : RoomDatabase() {
    abstract fun getMainIssuesPullsDao(): MainIssuesPullsDao
    abstract fun getNotifications(): NotificationsDao
    abstract fun getFeedsDao(): FeedDao
    abstract fun getUserDao(): UserDao
    abstract fun getUserRepoDao(): UserReposDao
}