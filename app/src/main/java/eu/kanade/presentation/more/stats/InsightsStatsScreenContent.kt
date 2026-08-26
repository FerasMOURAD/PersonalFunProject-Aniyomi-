package eu.kanade.presentation.more.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.stats.data.AchievementsSummary
import eu.kanade.presentation.more.stats.data.InsightsData
import eu.kanade.tachiyomi.ui.stats.achievements.AchievementsScreen

private fun formatDuration(ms: Long): String {
    val totalMinutes = ms / (1000L * 60)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 -> "${hours} h ${minutes} m"
        else -> "${minutes} m"
    }
}

@Composable
fun InsightsStatsScreenContent(
    state: StatsScreenState.SuccessInsights,
    paddingValues: PaddingValues,
) {
    val data = state.data
    val navigator = LocalNavigator.currentOrThrow

    LazyColumn(
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        // ── 1. Total Consumption Hero Header ──────────────────────────────
        item {
            TotalConsumptionHero(
                totalTimeMs = data.totalConsumptionMs,
                animeTimeMs = data.totalAnimeWatchTimeMs,
                mangaTimeMs = data.totalMangaReadTimeMs,
            )
        }

        // ── 2. Achievements & Milestones Showcase Card ────────────────────
        data.achievements?.let { achievements ->
            item {
                AchievementsShowcaseCard(
                    summary = achievements,
                    onClick = { navigator.push(AchievementsScreen(achievements)) },
                )
            }
        }

        // ── 3. Streak & 7-Day Activity ────────────────────────────────────
        item {
            StreakAndActivityCard(
                currentStreak = data.currentStreakDays,
                bestStreak = data.bestStreakDays,
                last7Days = data.last7DaysActivity,
            )
        }

        // ── 4. Time-of-Day Persona ────────────────────────────────────────
        item {
            TimeOfDayPersonaCard(
                habit = data.timeOfDayHabit,
            )
        }

        // ── 5. Top Genres Breakdown ───────────────────────────────────────
        if (data.topGenres.isNotEmpty()) {
            item {
                TopGenresCard(
                    topGenres = data.topGenres,
                )
            }
        }

        // ── 6. Consumption Highlights Grid ────────────────────────────────
        item {
            HighlightsGrid(
                completedAnime = data.completedAnimeCount,
                completedManga = data.completedMangaCount,
                episodesWatched = data.totalEpisodesWatched,
                chaptersRead = data.totalChaptersRead,
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TotalConsumptionHero(
    totalTimeMs: Long,
    animeTimeMs: Long,
    mangaTimeMs: Long,
) {
    val totalStr = remember(totalTimeMs) { formatDuration(totalTimeMs) }
    val animeStr = remember(animeTimeMs) { formatDuration(animeTimeMs) }
    val mangaStr = remember(mangaTimeMs) { formatDuration(mangaTimeMs) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
    ) {
        Text(
            text = totalStr,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Total consumption time",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "🎬 Anime: $animeStr",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "📖 Manga: $mangaStr",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun AchievementsShowcaseCard(
    summary: AchievementsSummary,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏆",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(end = 10.dp),
                    )
                    Column {
                        Text(
                            text = "Milestones & Badges",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "${summary.totalUnlocked} of ${summary.totalAchievements} Unlocked (${(summary.completionPercentage * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = "View all",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            LinearProgressIndicator(
                progress = { summary.completionPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round,
            )

            // Mini badges preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                summary.allAchievements.take(5).forEach { achievement ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (achievement.isUnlocked) {
                                    achievement.tier.color.copy(alpha = 0.2f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (achievement.isUnlocked) achievement.emoji else "🔒",
                            fontSize = 16.sp,
                        )
                    }
                }
                if (summary.allAchievements.size > 5) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "+${summary.allAchievements.size - 5}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakAndActivityCard(
    currentStreak: Int,
    bestStreak: Int,
    last7Days: List<InsightsData.DayActivity>,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 12.dp),
                    )
                    Column {
                        Text(
                            text = if (currentStreak == 1) "1 Day Streak" else "$currentStreak Days Streak",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Best streak: $bestStreak days",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Days Activity Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                last7Days.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = day.dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        day.isCompleted -> MaterialTheme.colorScheme.primary
                                        day.isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (day.isCompleted) {
                                Text(
                                    text = "✓",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeOfDayPersonaCard(
    habit: InsightsData.TimeOfDayHabit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = habit.persona.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp),
                )
                Column {
                    Text(
                        text = habit.persona.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = habit.persona.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time distribution breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TimeSlotItem(label = "🌅 Morning", percent = habit.morningPercent)
                TimeSlotItem(label = "☀️ Day", percent = habit.afternoonPercent)
                TimeSlotItem(label = "🌆 Evening", percent = habit.eveningPercent)
                TimeSlotItem(label = "🌙 Night", percent = habit.nightPercent)
            }
        }
    }
}

@Composable
private fun TimeSlotItem(label: String, percent: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun TopGenresCard(
    topGenres: List<InsightsData.GenreStat>,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "🎨 Favorite Genres",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            topGenres.forEach { genreStat ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = genreStat.genre,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "${genreStat.count} titles (${(genreStat.percentage * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    LinearProgressIndicator(
                        progress = { genreStat.percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        strokeCap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Composable
private fun HighlightsGrid(
    completedAnime: Int,
    completedManga: Int,
    episodesWatched: Int,
    chaptersRead: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HighlightMetricCard(
            title = "🏆 Completed Series",
            value = "${completedAnime + completedManga}",
            subtitle = "$completedAnime Anime • $completedManga Manga",
            modifier = Modifier.weight(1f),
        )
        HighlightMetricCard(
            title = "📑 Total Progress",
            value = "${episodesWatched + chaptersRead}",
            subtitle = "$episodesWatched Eps • $chaptersRead Chs",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HighlightMetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
