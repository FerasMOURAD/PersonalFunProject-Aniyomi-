package eu.kanade.tachiyomi.ui.stats.insights

import androidx.compose.ui.util.fastDistinctBy
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import eu.kanade.presentation.more.stats.StatsScreenState
import eu.kanade.presentation.more.stats.data.InsightsData
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.ui.stats.achievements.AchievementsEngine
import kotlinx.coroutines.flow.update
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.data.handlers.anime.AnimeDatabaseHandler
import tachiyomi.data.handlers.manga.MangaDatabaseHandler
import tachiyomi.domain.entries.anime.interactor.GetLibraryAnime
import tachiyomi.domain.entries.manga.interactor.GetLibraryManga
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

class InsightsStatsScreenModel(
    private val getAnimelibAnime: GetLibraryAnime = Injekt.get(),
    private val getLibraryManga: GetLibraryManga = Injekt.get(),
    private val animeHandler: AnimeDatabaseHandler = Injekt.get(),
    private val mangaHandler: MangaDatabaseHandler = Injekt.get(),
) : StateScreenModel<StatsScreenState>(StatsScreenState.Loading) {

    init {
        calculateInsights()
    }

    fun calculateInsights() {
        screenModelScope.launchIO {
            val animelibAnime = getAnimelibAnime.await().fastDistinctBy { it.id }
            val libraryManga = getLibraryManga.await().fastDistinctBy { it.id }

            val watchTimes = animeHandler.awaitList { animesQueries.getWatchTimes() }
            val totalAnimeWatchTimeMs = watchTimes.sumOf { it.watchTime.toLong() }

            val readMangas = mangaHandler.awaitList { mangasQueries.getReadManga() }
            val totalMangaReadTimeMs = readMangas.sumOf { it.total_read_duration }

            val completedAnimeCount = animelibAnime.count {
                it.anime.status.toInt() == SAnime.COMPLETED && it.unseenCount == 0L
            }
            val completedMangaCount = libraryManga.count {
                it.manga.status.toInt() == SManga.COMPLETED && it.unreadCount == 0L
            }

            val totalEpisodesWatched = animelibAnime.sumOf { it.seenCount }.toInt()
            val totalChaptersRead = libraryManga.sumOf { it.readCount }.toInt()

            // ── Top Genres ─────────────────────────────────────────────────────
            val allGenres = (animelibAnime.flatMap { it.anime.genre.orEmpty() } + libraryManga.flatMap { it.manga.genre.orEmpty() })
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val genreCounts = allGenres.groupingBy { it }.eachCount()
            val totalGenreOccurrences = genreCounts.values.sum().toFloat().coerceAtLeast(1f)
            val topGenres = genreCounts.entries
                .sortedByDescending { it.value }
                .take(5)
                .map { (genre, count) ->
                    InsightsData.GenreStat(
                        genre = genre,
                        count = count,
                        percentage = count / totalGenreOccurrences,
                    )
                }

            // ── Activity, Streaks, & Time of Day ───────────────────────────────
            val animeHistory = animeHandler.awaitList { animehistoryViewQueries.animehistory("") }
            val mangaHistory = mangaHandler.awaitList { historyViewQueries.history("") }

            val allDatesWithTime = mutableListOf<Date>()
            animeHistory.forEach { entry ->
                entry.seenAt?.let { allDatesWithTime.add(it) }
            }
            mangaHistory.forEach { entry ->
                entry.readAt?.let { allDatesWithTime.add(it) }
            }

            val systemZone = ZoneId.systemDefault()
            val today = LocalDate.now(systemZone)

            val localDateSet = allDatesWithTime
                .filter { it.time > 0 }
                .map { Instant.ofEpochMilli(it.time).atZone(systemZone).toLocalDate() }
                .toSet()

            // Calculate current streak
            var currentStreak = 0
            val checkDate = when {
                localDateSet.contains(today) -> today
                localDateSet.contains(today.minusDays(1)) -> today.minusDays(1)
                else -> null
            }
            if (checkDate != null) {
                var d: LocalDate? = checkDate
                while (d != null && localDateSet.contains(d)) {
                    currentStreak++
                    d = d.minusDays(1)
                }
            }

            // Calculate longest streak
            val sortedDates = localDateSet.sorted()
            var longestStreak = 0
            var tempStreak = 0
            var prevDate: LocalDate? = null
            for (date in sortedDates) {
                val prev = prevDate
                if (prev != null && date == prev.plusDays(1)) {
                    tempStreak++
                } else {
                    tempStreak = 1
                }
                if (tempStreak > longestStreak) {
                    longestStreak = tempStreak
                }
                prevDate = date
            }
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak
            }

            // 7 Days Activity (Mon-Sun or past 7 days)
            val last7Days = (6 downTo 0).map { daysAgo ->
                val targetDate = today.minusDays(daysAgo.toLong())
                val dayOfWeekShort = targetDate.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
                InsightsData.DayActivity(
                    dayLabel = dayOfWeekShort,
                    isCompleted = localDateSet.contains(targetDate),
                    isToday = daysAgo == 0,
                )
            }

            // Time of Day Persona
            val hours = allDatesWithTime
                .filter { it.time > 0 }
                .map { Instant.ofEpochMilli(it.time).atZone(systemZone).hour }

            val totalEvents = hours.size.toFloat().coerceAtLeast(1f)
            val morningCount = hours.count { it in 5..11 }
            val afternoonCount = hours.count { it in 12..16 }
            val eveningCount = hours.count { it in 17..21 }
            val nightCount = hours.count { it >= 22 || it < 5 }

            val morningPercent = ((morningCount / totalEvents) * 100).toInt()
            val afternoonPercent = ((afternoonCount / totalEvents) * 100).toInt()
            val eveningPercent = ((eveningCount / totalEvents) * 100).toInt()
            val nightPercent = ((nightCount / totalEvents) * 100).toInt()

            val persona = when {
                nightPercent >= 35 -> InsightsData.Persona.NIGHT_OWL
                morningPercent >= 30 -> InsightsData.Persona.EARLY_BIRD
                eveningPercent >= 30 -> InsightsData.Persona.EVENING_BINGER
                afternoonPercent >= 30 -> InsightsData.Persona.AFTERNOON_CHILLER
                else -> InsightsData.Persona.BALANCED
            }

            val habit = InsightsData.TimeOfDayHabit(
                persona = persona,
                morningPercent = morningPercent,
                afternoonPercent = afternoonPercent,
                eveningPercent = eveningPercent,
                nightPercent = nightPercent,
            )

            // ── Achievements & Milestones Calculation ─────────────────────────
            val topGenreCount = topGenres.firstOrNull()?.count ?: 0
            val achievements = AchievementsEngine.compute(
                currentStreakDays = currentStreak,
                bestStreakDays = longestStreak,
                totalAnimeWatchTimeMs = totalAnimeWatchTimeMs,
                totalMangaReadTimeMs = totalMangaReadTimeMs,
                episodesWatched = totalEpisodesWatched,
                chaptersRead = totalChaptersRead,
                completedAnimeCount = completedAnimeCount,
                completedMangaCount = completedMangaCount,
                nightSessionsCount = nightCount,
                morningSessionsCount = morningCount,
                topGenreCount = topGenreCount,
            )

            val insights = InsightsData(
                totalConsumptionMs = totalAnimeWatchTimeMs + totalMangaReadTimeMs,
                totalAnimeWatchTimeMs = totalAnimeWatchTimeMs,
                totalMangaReadTimeMs = totalMangaReadTimeMs,
                totalEpisodesWatched = totalEpisodesWatched,
                totalChaptersRead = totalChaptersRead,
                completedAnimeCount = completedAnimeCount,
                completedMangaCount = completedMangaCount,
                currentStreakDays = currentStreak,
                bestStreakDays = longestStreak,
                last7DaysActivity = last7Days,
                timeOfDayHabit = habit,
                topGenres = topGenres,
                achievements = achievements,
            )

            mutableState.update {
                StatsScreenState.SuccessInsights(insights)
            }
        }
    }
}
