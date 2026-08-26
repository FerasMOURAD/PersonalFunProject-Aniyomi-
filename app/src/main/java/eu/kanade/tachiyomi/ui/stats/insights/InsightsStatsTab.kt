package eu.kanade.tachiyomi.ui.stats.insights

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.TabContent
import eu.kanade.presentation.more.stats.InsightsStatsScreenContent
import eu.kanade.presentation.more.stats.StatsScreenState
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.screens.LoadingScreen

@Composable
fun Screen.insightsStatsTab(): TabContent {
    val navigator = LocalNavigator.currentOrThrow

    val screenModel = rememberScreenModel { InsightsStatsScreenModel() }
    val state by screenModel.state.collectAsState()

    if (state is StatsScreenState.Loading) {
        LoadingScreen()
    }

    return TabContent(
        titleRes = MR.strings.label_overview_section,
        content = { contentPadding, _ ->
            if (state is StatsScreenState.Loading) {
                LoadingScreen()
            } else {
                InsightsStatsScreenContent(
                    state = state as StatsScreenState.SuccessInsights,
                    paddingValues = contentPadding,
                )
            }
        },
        navigateUp = navigator::pop,
    )
}
