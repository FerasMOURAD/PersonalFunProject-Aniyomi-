package eu.kanade.domain.items.chapter.model

import eu.kanade.tachiyomi.data.database.models.manga.ChapterImpl
import eu.kanade.tachiyomi.source.model.ChapterMemoManager
import eu.kanade.tachiyomi.source.model.SChapter
import tachiyomi.domain.items.chapter.model.Chapter
import eu.kanade.tachiyomi.data.database.models.manga.Chapter as DbChapter

// TODO: Remove when all deps are migrated
fun Chapter.toSChapter(): SChapter {
    return SChapter.create().also {
        it.url = url
        it.name = name
        it.date_upload = dateUpload
        it.chapter_number = chapterNumber.toFloat()
        it.scanlator = scanlator
        it.memo = ChapterMemoManager.getMemo(url)
    }
}

fun Chapter.copyFromSChapter(sChapter: SChapter): Chapter {
    if (sChapter.memo.isNotEmpty()) {
        ChapterMemoManager.putMemo(sChapter.url, sChapter.memo)
    }
    return this.copy(
        name = sChapter.name,
        url = sChapter.url,
        dateUpload = sChapter.date_upload,
        chapterNumber = sChapter.chapter_number.toDouble(),
        scanlator = sChapter.scanlator?.ifBlank { null }?.trim(),
    )
}

fun Chapter.toDbChapter(): DbChapter = ChapterImpl().also {
    it.id = id
    it.manga_id = mangaId
    it.url = url
    it.name = name
    it.scanlator = scanlator
    it.read = read
    it.bookmark = bookmark
    it.last_page_read = lastPageRead.toInt()
    it.date_fetch = dateFetch
    it.date_upload = dateUpload
    it.chapter_number = chapterNumber.toFloat()
    it.source_order = sourceOrder.toInt()
    it.memo = ChapterMemoManager.getMemo(url)
}
