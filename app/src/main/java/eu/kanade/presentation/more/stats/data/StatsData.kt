package eu.kanade.presentation.more.stats.data

import tachiyomi.domain.entries.EntryCover

sealed interface StatsData {

    data class MangaOverview(
        val libraryMangaCount: Int,
        val completedMangaCount: Int,
        val totalReadDuration: Long,
    ) : StatsData

    data class AnimeOverview(
        val libraryAnimeCount: Int,
        val completedAnimeCount: Int,
        val totalSeenDuration: Long,
    ) : StatsData

    data class MangaTitles(
        val globalUpdateItemCount: Int,
        val startedMangaCount: Int,
        val localMangaCount: Int,
    ) : StatsData

    data class AnimeTitles(
        val globalUpdateItemCount: Int,
        val startedAnimeCount: Int,
        val localAnimeCount: Int,
    ) : StatsData

    data class Chapters(
        val totalChapterCount: Int,
        val readChapterCount: Int,
        val downloadCount: Int,
    ) : StatsData

    data class Episodes(
        val totalEpisodeCount: Int,
        val readEpisodeCount: Int,
        val downloadCount: Int,
    ) : StatsData

    data class Trackers(
        val trackedTitleCount: Int,
        val meanScore: Double,
        val trackerCount: Int,
    ) : StatsData

    /**
     * Represents a single library entry's total read/watch duration for the bar chart.
     * [durationMs] is in milliseconds for both manga and anime.
     */
    data class EntryTimeStat(
        val id: Long,
        val title: String,
        val coverData: EntryCover,
        val durationMs: Long,
    )

    data class MangaEntryTimeList(
        val entries: List<EntryTimeStat>,
        val totalDurationMs: Long,
    ) : StatsData

    data class AnimeEntryTimeList(
        val entries: List<EntryTimeStat>,
        val totalDurationMs: Long,
    ) : StatsData
}
