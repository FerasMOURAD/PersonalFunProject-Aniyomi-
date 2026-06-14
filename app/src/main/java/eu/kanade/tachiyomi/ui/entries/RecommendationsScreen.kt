package eu.kanade.tachiyomi.ui.entries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import eu.kanade.tachiyomi.network.services.AniListRecommendation
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import eu.kanade.tachiyomi.ui.browse.manga.source.globalsearch.GlobalMangaSearchScreen
import eu.kanade.tachiyomi.ui.browse.anime.source.globalsearch.GlobalAnimeSearchScreen
import eu.kanade.presentation.components.AppBar

data class RecommendationsScreen(
    val title: String,
    val recommendations: List<AniListRecommendation>,
    val isManga: Boolean
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                AppBar(
                    title = "Suggestions for ${this.title}",
                    navigateUp = { navigator.pop() }
                )
            }
        ) { contentPadding ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                contentPadding = PaddingValues(
                    start = MaterialTheme.padding.medium,
                    end = MaterialTheme.padding.medium,
                    top = contentPadding.calculateTopPadding() + MaterialTheme.padding.small,
                    bottom = contentPadding.calculateBottomPadding() + MaterialTheme.padding.small
                ),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small)
            ) {
                items(recommendations) { recommendation ->
                    RecommendationGridItem(
                        recommendation = recommendation,
                        onClick = { 
                            if (isManga) {
                                navigator.push(GlobalMangaSearchScreen(recommendation.title))
                            } else {
                                navigator.push(GlobalAnimeSearchScreen(recommendation.title))
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun RecommendationGridItem(
        recommendation: AniListRecommendation,
        onClick: () -> Unit,
    ) {
        Column(
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            AsyncImage(
                model = recommendation.coverImage,
                contentDescription = recommendation.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
