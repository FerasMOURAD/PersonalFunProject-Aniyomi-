package eu.kanade.presentation.more.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.entries.components.ItemCover
import eu.kanade.presentation.more.stats.data.StatsData
import eu.kanade.presentation.util.toDurationString
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private val BAR_HEIGHT = 52.dp
private val COVER_SIZE = 36.dp
private val MIN_INFO_WIDTH = 130.dp

/** Curated dark muted bar colors, inspired by the reference image palette. */
private val BAR_COLORS = listOf(
    Color(0xFF2D3561), // dark slate blue
    Color(0xFF3A1C1C), // dark maroon
    Color(0xFF1A3A3A), // dark teal
    Color(0xFF2D1B4E), // dark purple
    Color(0xFF1A3D2E), // dark forest green
    Color(0xFF3D2802), // dark burnt orange
    Color(0xFF0D2137), // dark navy
    Color(0xFF3A2D1A), // dark caramel
    Color(0xFF1E2456), // dark indigo
    Color(0xFF3A1A2D), // dark burgundy
    Color(0xFF1A2D3A), // dark steel blue
    Color(0xFF2D3A1A), // dark olive
)

@Composable
fun EntryTimeBarItem(
    entry: StatsData.EntryTimeStat,
    maxDuration: Long,
    modifier: Modifier = Modifier,
) {
    val fraction = if (maxDuration > 0) {
        (entry.durationMs.toFloat() / maxDuration.toFloat()).coerceIn(0.02f, 1f)
    } else {
        0.02f
    }

    val context = LocalContext.current
    val none = stringResource(MR.strings.none)
    val durationText = remember(entry.durationMs) {
        entry.durationMs.toDuration(DurationUnit.MILLISECONDS)
            .toDurationString(context, fallback = none)
    }

    val barColor = remember(entry.id) {
        BAR_COLORS[(entry.id % BAR_COLORS.size).toInt().coerceAtLeast(0)]
    }

    BoxWithConstraints(
        modifier = modifier.height(BAR_HEIGHT),
    ) {
        val totalWidth = maxWidth
        // Bar ends here; clamp so the info column always has MIN_INFO_WIDTH
        val barEndX = (totalWidth * fraction).coerceAtMost(totalWidth - MIN_INFO_WIDTH)

        // ── Colored bar background ────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(barEndX)
                .fillMaxHeight()
                .background(
                    color = barColor,
                    shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
                ),
        )

        // ── Cover thumbnail — pinned to the right end of the bar ──────────
        Box(
            modifier = Modifier
                .width(barEndX)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            ItemCover.Book(
                data = entry.coverData,
                modifier = Modifier
                    .width(COVER_SIZE)
                    .fillMaxHeight()
                    .padding(vertical = 1.dp),
            )
        }

        // ── Info column — starts RIGHT after the bar end ──────────────────
        Box(
            modifier = Modifier
                .padding(start = barEndX + 8.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Column {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = durationText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}
