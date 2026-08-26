package eu.kanade.presentation.more.stats.data

import androidx.compose.runtime.Immutable

@Immutable
data class InsightsData(
    val totalConsumptionMs: Long,
    val totalAnimeWatchTimeMs: Long,
    val totalMangaReadTimeMs: Long,
    val totalEpisodesWatched: Int,
    val totalChaptersRead: Int,
    val completedAnimeCount: Int,
    val completedMangaCount: Int,
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val last7DaysActivity: List<DayActivity>,
    val timeOfDayHabit: TimeOfDayHabit,
    val topGenres: List<GenreStat>,
    val achievements: AchievementsSummary? = null,
) {
    @Immutable
    data class DayActivity(
        val dayLabel: String,
        val isCompleted: Boolean,
        val isToday: Boolean = false,
    )

    enum class Persona(val title: String, val emoji: String, val subtitle: String) {
        NIGHT_OWL("Night Owl", "🦉", "Most of your activity happens late at night (10 PM - 5 AM)."),
        EARLY_BIRD("Early Bird", "🌅", "You love starting your morning with anime & manga (5 AM - 12 PM)."),
        AFTERNOON_CHILLER("Afternoon Chiller", "☀️", "You love taking midday breaks with your favorite titles (12 PM - 5 PM)."),
        EVENING_BINGER("Evening Binger", "🌆", "Evenings are your prime entertainment hours (5 PM - 10 PM)."),
        BALANCED("Balanced Rhythm", "⚖️", "You enjoy stories steadily throughout the day."),
    }

    @Immutable
    data class TimeOfDayHabit(
        val persona: Persona,
        val morningPercent: Int,
        val afternoonPercent: Int,
        val eveningPercent: Int,
        val nightPercent: Int,
    )

    @Immutable
    data class GenreStat(
        val genre: String,
        val count: Int,
        val percentage: Float,
    )
}
