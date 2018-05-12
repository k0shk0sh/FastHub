package com.fastaccess.data.persistence.db

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * Created by Kosh on 11.05.18.
 */

class DateConverter {
    @TypeConverter fun toDbValue(date: Date? = null): Long? = date?.time
    @TypeConverter fun fromDbToValue(date: Long? = 0): Date? = date?.let { Date(it) }
}