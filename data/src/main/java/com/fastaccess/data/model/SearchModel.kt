package com.fastaccess.data.model

import com.fastaccess.data.model.parcelable.FilterIssuesPrsModel
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.data.persistence.models.ProfileRepoModel

/**
 * Created by Kosh on 21.01.19.
 */
data class SearchModel(val issuesPrsModel: ArrayList<MyIssuesPullsModel>? = null,
                       val repoModel: ArrayList<ProfileRepoModel>? = null)