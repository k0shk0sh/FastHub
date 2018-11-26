package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.OrganizationModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class OrgsDao : BaseDao<OrganizationModel>() {
    @Query("SELECT * FROM ${OrganizationModel.TABLE_NAME}")
    abstract fun getOrgs():  DataSource.Factory<Int, OrganizationModel>

    @Query("SELECT * FROM ${OrganizationModel.TABLE_NAME} WHERE `id` = :id")
    abstract fun getOrg(id: String): LiveData<OrganizationModel>

    @Query("SELECT * FROM ${OrganizationModel.TABLE_NAME} WHERE `id` = :id")
    abstract fun getOrgBlocking(id: String): OrganizationModel?

    @Query("DELETE FROM ${OrganizationModel.TABLE_NAME}") abstract fun deleteAll()
}