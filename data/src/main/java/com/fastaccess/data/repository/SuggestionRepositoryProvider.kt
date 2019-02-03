package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.fastaccess.data.persistence.dao.SuggestionDao
import com.fastaccess.data.persistence.models.SuggestionsModel
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */

class SuggestionRepositoryProvider @Inject constructor(
    private val dao: SuggestionDao
) : SuggestionRepository {
    override fun getAll(): DataSource.Factory<Int, SuggestionsModel> = dao.getAll()
    override fun getSuggestions(keyword: String): LiveData<List<SuggestionsModel>> = dao.getSuggestions(keyword)
    override fun upsert(query: String): Completable = Completable.fromCallable { dao.upsert(SuggestionsModel(keywordWord = query)) }
    override fun deleteAll(): Completable = Completable.fromCallable { dao.deleteAll() }
}

interface SuggestionRepository {
    fun getAll(): DataSource.Factory<Int, SuggestionsModel>
    fun getSuggestions(keyword: String):  LiveData<List<SuggestionsModel>>
    fun upsert(query: String): Completable
    fun deleteAll(): Completable
}
