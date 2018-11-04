package com.fastaccess.data.persistence.dao

import androidx.room.*
import timber.log.Timber

/**
 * Created by Kosh on 07.07.18.
 */

@Dao
abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE) abstract fun insert(obj: T): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE) abstract fun insert(obj: List<T>): List<Long>
    @Update abstract fun update(obj: T)
    @Update abstract fun update(obj: List<T>)
    @Delete abstract fun delete(obj: T)

    @Transaction open fun upsert(obj: T) {
        val id = insert(obj)
        Timber.e("upsert($id) if id == -1 then we are proceeding with update")
        if (id == -1L) {
            update(obj)
        }
    }

    @Transaction open fun upsert(objList: List<T>) {
        val insertResult = insert(objList)
        val updateList = arrayListOf<T>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                updateList.add(objList[i])
            }
        }

        if (!updateList.isEmpty()) {
            update(updateList)
        }
    }
}