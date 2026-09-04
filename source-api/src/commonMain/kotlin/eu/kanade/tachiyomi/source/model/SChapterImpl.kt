@file:Suppress("PropertyName")

package eu.kanade.tachiyomi.source.model

import kotlinx.serialization.json.JsonObject

class SChapterImpl : SChapter {

    override lateinit var url: String

    override lateinit var name: String

    override var date_upload: Long = 0

    override var chapter_number: Float = -1f

    override var scanlator: String? = null

    private var _memo: JsonObject? = null

    override var memo: JsonObject
        get() {
            val m = _memo
            if (m != null && m.isNotEmpty()) return m
            return if (::url.isInitialized) ChapterMemoManager.getMemo(url) else JsonObject(emptyMap())
        }
        set(value) {
            _memo = value
            if (value.isNotEmpty() && ::url.isInitialized) {
                ChapterMemoManager.putMemo(url, value)
            }
        }
}
