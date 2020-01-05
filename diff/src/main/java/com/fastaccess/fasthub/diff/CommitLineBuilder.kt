package com.fastaccess.fasthub.diff

import com.fastaccess.data.model.CommitLinesModel
import com.fastaccess.data.model.CommitLinesModel.Companion.ADDITION
import com.fastaccess.data.model.CommitLinesModel.Companion.DELETION
import com.fastaccess.data.model.CommitLinesModel.Companion.PATCH
import com.fastaccess.data.model.CommitLinesModel.Companion.TRANSPARENT
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs

object CommitLineBuilder {
    private val HUNK_TITLE = Pattern.compile("^.*-([0-9]+)(?:,([0-9]+))? \\+([0-9]+)(?:,([0-9]+))?.*$")
    fun buildLines(patch: String?): List<CommitLinesModel> {
        val models = ArrayList<CommitLinesModel>()
        if (!patch.isNullOrEmpty()) {
            val split = patch.split("\\r?\\n|\\r".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split.size > 1) {
                var leftLineNo = -1
                var rightLineNo = -1
                var position = 0
                for (text in split) {
                    var _text: String = text
                    val firstChar = _text[0]
                    var addLeft = false
                    var addRight = false
                    var color = TRANSPARENT
                    if (_text.startsWith("@@")) {
                        color = PATCH
                        val matcher = HUNK_TITLE.matcher(_text.trim { it <= ' ' })
                        if (matcher.matches()) {
                            try {
                                leftLineNo = abs(matcher.group(1)?.toIntOrNull() ?: 0) - 1
                                rightLineNo = (matcher.group(3)?.toIntOrNull() ?: 0) - 1
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                            }

                        }
                    } else if (firstChar == '+') {
                        position++
                        color = ADDITION
                        ++rightLineNo
                        addRight = true
                        addLeft = false
                    } else if (firstChar == '-') {
                        position++
                        color = DELETION
                        ++leftLineNo
                        addRight = false
                        addLeft = true
                    } else {
                        position++
                        addLeft = true
                        addRight = true
                        ++rightLineNo
                        ++leftLineNo
                    }
                    val index = _text.indexOf("\\ No newline at end of file")
                    if (index != -1) {
                        _text = _text.replace("\\ No newline at end of file", "")
                    }
                    models.add(
                        CommitLinesModel(
                            _text.replace("\t", ""), color, if (_text.startsWith("@@") || !addLeft) -1 else leftLineNo,
                            if (_text.startsWith("@@") || !addRight) -1 else rightLineNo, index != -1, position, false
                        )
                    )
                }
            }
        }
        return models
    }
}