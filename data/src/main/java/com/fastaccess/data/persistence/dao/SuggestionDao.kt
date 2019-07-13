package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.SuggestionsModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class SuggestionDao : BaseDao<SuggestionsModel>() {
    @Query("SELECT * FROM ${SuggestionsModel.TABLE_NAME}")
    abstract fun getAll(): DataSource.Factory<Int, SuggestionsModel>

    @Query("SELECT * FROM ${SuggestionsModel.TABLE_NAME} WHERE `keywordWord` LIKE '%' || :keyword || '%' LIMIT 30")
    abstract fun getSuggestions(keyword: String): LiveData<List<SuggestionsModel>>

    @Query("DELETE FROM ${SuggestionsModel.TABLE_NAME}") abstract fun deleteAll()
}