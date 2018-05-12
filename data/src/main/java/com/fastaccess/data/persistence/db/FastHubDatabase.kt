package com.fastaccess.data.persistence.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.fastaccess.data.persistence.models.UserModel

/**
 * Created by Kosh on 11.05.18.
 */

const val VERSION = 2
const val DATABASE_NAME = "FastHub-Room-DB"

@Database(version = VERSION, entities = [UserModel::class], exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FastHubDatabase : RoomDatabase()