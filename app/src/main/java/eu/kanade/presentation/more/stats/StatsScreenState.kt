package eu.kanade.presentation.more.stats

import androidx.compose.runtime.Immutable
import eu.kanade.presentation.more.stats.data.InsightsData
import eu.kanade.presentation.more.stats.data.StatsData

sealed interface StatsScreenState {
    @Immutable
    data object Loading : StatsScreenState

    @Immutable
    data class SuccessInsights(
        val data: InsightsData,
    ) : StatsScreenState

    @Immutable
    data class SuccessManga(
        val overview: StatsData.MangaOverview,
        val titles: StatsData.MangaTitles,
        val chapters: StatsData.Chapters,
        val trackers: StatsData.Trackers,
        val entryTimes: StatsData.MangaEntryTimeList,
    ) : StatsScreenState

    @Immutable
    data class SuccessAnime(
        val overview: StatsData.AnimeOverview,
        val titles: StatsData.AnimeTitles,
        val episodes: StatsData.Episodes,
        val trackers: StatsData.Trackers,
        val entryTimes: StatsData.AnimeEntryTimeList,
    ) : StatsScreenState
}
