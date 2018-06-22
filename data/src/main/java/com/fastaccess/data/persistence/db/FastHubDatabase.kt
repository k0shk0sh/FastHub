package com.fastaccess.data.persistence.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fastaccess.data.persistence.dao.MainIssuesPullsDao
import com.fastaccess.data.persistence.dao.NotificationsDao
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.data.persistence.models.UserModel

/**
 * Created by Kosh on 11.05.18.
 */

const val VERSION = 2
const val DATABASE_NAME = "FastHub-Room-DB"

@Database(version = VERSION, entities = [UserModel::class, MainIssuesPullsModel::class, NotificationModel::class], exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FastHubDatabase : RoomDatabase() {
    abstract fun getMainIssuesPullsDao(): MainIssuesPullsDao
    abstract fun getNotifications(): NotificationsDao
}