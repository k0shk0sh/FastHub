package com.fastaccess.data.persistence.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fastaccess.data.persistence.models.UserModel

/**
 * Created by Kosh on 11.05.18.
 */

const val VERSION = 2
const val DATABASE_NAME = "FastHub-Room-DB"

@Database(version = VERSION, entities = [UserModel::class], exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FastHubDatabase : RoomDatabase()