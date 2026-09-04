package eu.kanade.tachiyomi.source.model

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.concurrent.ConcurrentHashMap

object ChapterMemoManager {

    private val memoByUrl = ConcurrentHashMap<String, JsonObject>()
    private val memoByBaseSlug = ConcurrentHashMap<String, JsonObject>()

    fun putMemo(url: String, memo: JsonObject) {
        if (memo.isEmpty()) return
        memoByUrl[url] = memo
        val baseSlug = extractBaseSlug(url)
        if (baseSlug != null) {
            memoByBaseSlug[baseSlug] = memo
        }
    }

    fun putMangaSlug(baseSlug: String, resolvedSlug: String) {
        if (baseSlug.isBlank() || resolvedSlug.isBlank()) return
        val memo = buildJsonObject {
            put("mangaSlug", resolvedSlug)
        }
        memoByBaseSlug[baseSlug] = memo
    }

    fun getMemo(url: String): JsonObject {
        memoByUrl[url]?.let { return it }
        val baseSlug = extractBaseSlug(url)
        if (baseSlug != null) {
            memoByBaseSlug[baseSlug]?.let { return it }
            // If no memo was stored yet, synthesize one from baseSlug
            return buildJsonObject {
                put("mangaSlug", baseSlug)
            }
        }
        return JsonObject(emptyMap())
    }

    fun extractBaseSlug(url: String): String? {
        val raw = when {
            url.contains("/series/") -> url.substringAfter("/series/").substringBefore("/chapter/")
            url.contains("/comics/") -> url.substringAfter("/comics/").substringBefore("/chapter/")
            url.contains("/manga/") -> url.substringAfter("/manga/").substringBefore("/chapter/")
            else -> null
        }?.trim('/')
        return raw?.takeIf { it.isNotBlank() }
    }
}
