package com.fastaccess.data.model

data class CommitLinesModel(
    var text: String? = null,
    var color: Int = 0,
    var leftLineNo: Int = 0,
    var rightLineNo: Int = 0,
    var noNewLine: Boolean = false,
    var position: Int = 0,
    var hasCommentedOn: Boolean = false
) {

    companion object {
        const val TRANSPARENT = 0
        const val ADDITION = 1
        const val DELETION = 2
        const val PATCH = 3
    }
}