package com.fastaccess.domain.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 16.02.19.
 */
data class IssueRequestModel(
    @SerializedName("title") var title: String? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("milestone") var milestone: Int? = null,
    @SerializedName("assignees") var assignees: ArrayList<String>? = null,
    @SerializedName("labels") var labels: ArrayList<String>? = null,
    @SerializedName("state") var state: String? = null
) {
    companion object {
        fun newInstance(title: String? = null,
                        body: String? = null,
                        milestone: Int? = null,
                        assignees: ArrayList<String>? = null,
                        labels: ArrayList<String>? = null,
                        state: String? = null): IssueRequestModel = IssueRequestModel(title, body, milestone, assignees, labels, state)
    }
}