package com.fastaccess.data.persistence.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fastaccess.data.persistence.dao.LoginDao
import com.fastaccess.data.persistence.models.LoginModel

/**
 * Created by Kosh on 11.05.18.
 */

const val LOGIN_VERSION = 1
const val LOGIN_DATABASE_NAME = "FastHub-Login-Room-DB"

@Database(version = LOGIN_VERSION, entities = [LoginModel::class], exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FastHubLoginDatabase : RoomDatabase() {
    abstract fun provideLoginDao(): LoginDao
}