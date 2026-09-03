package eu.kanade.tachiyomi.source.model

import java.io.Serializable

data class SMangaUpdate(
    val manga: SManga,
    val chapters: List<SChapter>,
) : Serializable
