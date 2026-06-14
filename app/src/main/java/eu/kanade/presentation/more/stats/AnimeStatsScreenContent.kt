package eu.kanade.presentation.more.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.more.stats.components.EntryTimeBarItem
import eu.kanade.presentation.more.stats.data.StatsData

/** Formats [ms] as "X h Y m", "Y m", etc. — matching the reference image style. */
private fun formatTotalDuration(ms: Long): String {
    val totalMinutes = ms / (1000L * 60)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 -> "${hours} h ${minutes} m"
        else -> "${minutes} m"
    }
}

@Composable
fun AnimeStatsScreenContent(
    state: StatsScreenState.SuccessAnime,
    paddingValues: PaddingValues,
) {
    val maxDuration = remember(state.entryTimes.entries) {
        state.entryTimes.entries.maxOfOrNull { it.durationMs } ?: 1L
    }

    LazyColumn(
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // ── Total time header ──────────────────────────────────────────────
        item {
            TotalTimeHeader(
                totalDurationMs = state.entryTimes.totalDurationMs,
                label = "Total watch time",
            )
        }

        // ── Bar chart rows ─────────────────────────────────────────────────
        items(state.entryTimes.entries, key = { it.id }) { entry ->
            EntryTimeBarItem(
                entry = entry,
                maxDuration = maxDuration,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TotalTimeHeader(totalDurationMs: Long, label: String) {
    val timeText = remember(totalDurationMs) { formatTotalDuration(totalDurationMs) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 28.dp),
    ) {
        Text(
            text = timeText,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
