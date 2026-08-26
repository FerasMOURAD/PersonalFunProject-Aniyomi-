package eu.kanade.presentation.more.stats.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

enum class AchievementTier(val title: String, val color: Color) {
    BRONZE("Bronze", Color(0xFFCD7F32)),
    SILVER("Silver", Color(0xFFB0BEC5)),
    GOLD("Gold", Color(0xFFFFD700)),
    PLATINUM("Platinum", Color(0xFF4DD0E1)),
    DIAMOND("Diamond", Color(0xFFB388FF)),
}

enum class AchievementCategory(val title: String, val emoji: String) {
    ALL("All", "🌟"),
    STREAKS("Streaks", "🔥"),
    ANIME_TIME("Anime", "🎬"),
    MANGA_TIME("Manga", "📖"),
    VOLUME("Volume", "📚"),
    HABITS("Habits", "🦉"),
}

@Immutable
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val tier: AchievementTier,
    val emoji: String,
    val currentValue: Long,
    val targetValue: Long,
    val unit: String,
) {
    val isUnlocked: Boolean get() = currentValue >= targetValue
    val progress: Float get() = if (targetValue <= 0) 1f else (currentValue.toFloat() / targetValue).coerceIn(0f, 1f)
}

@Immutable
data class AchievementsSummary(
    val totalUnlocked: Int,
    val totalAchievements: Int,
    val completionPercentage: Float,
    val allAchievements: List<Achievement>,
    val recentUnlocked: List<Achievement>,
    val nextToUnlock: List<Achievement>,
)
