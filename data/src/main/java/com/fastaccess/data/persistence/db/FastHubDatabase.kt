package com.fastaccess.data.persistence.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fastaccess.data.persistence.dao.FeedDao
import com.fastaccess.data.persistence.dao.MainIssuesPullsDao
import com.fastaccess.data.persistence.dao.NotificationsDao
import com.fastaccess.data.persistence.dao.UserDao
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.data.persistence.models.UserModel

/**
 * Created by Kosh on 11.05.18.
 */

const val VERSION = 3
const val DATABASE_NAME = "FastHub-Room-DB"

@Database(version = VERSION, entities = [UserModel::class, MainIssuesPullsModel::class,
    NotificationModel::class, FeedModel::class], exportSchema = false)
@TypeConverters(DateConverter::class, EventTypesConverter::class,
        FeedPayloadConverter::class, FeedRepoConverter::class,
        UserCountConverter::class, UserOrganizationConverter::class)
abstract class FastHubDatabase : RoomDatabase() {
    abstract fun getMainIssuesPullsDao(): MainIssuesPullsDao
    abstract fun getNotifications(): NotificationsDao
    abstract fun getFeedsDao(): FeedDao
    abstract fun getUserDao(): UserDao
}