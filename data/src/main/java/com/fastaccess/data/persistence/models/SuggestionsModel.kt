package com.fastaccess.data.persistence.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by Kosh on 03.02.19.
 */
@Entity(tableName = SuggestionsModel.TABLE_NAME, indices = [Index(value = ["keywordWord"], unique = true)])
data class SuggestionsModel(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                            var keywordWord: String) {

    override fun toString(): String {
        return keywordWord
    }

    companion object {
        const val TABLE_NAME = "suggestion_table"
    }
}