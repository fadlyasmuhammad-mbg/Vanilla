@file:OptIn(ExperimentalUuidApi::class)

package com.fadlyas07.fadencecalc.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.fadlyas07.fadencecalc.R
import com.fadlyas07.fadencecalc.ui.navigation.Screens
import com.fadlyas07.fadencecalc.ui.navigation.SettingsScreen
import com.fadlyas07.fadencecalc.ui.screens.settings.components.AboutCard
import com.fadlyas07.fadencecalc.ui.screens.settings.components.SettingsCategoryCard
import com.fadlyas07.fadencecalc.ui.shared_components.AnimatedFab
import com.fadlyas07.fadencecalc.utils.bouncySpec
import com.fadlyas07.fadencecalc.utils.navigationBouncySpec
import com.fadlyas07.fadencecalc.utils.selfAlignHorizontally
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun SettingsScreen(
    onNavigate: (Screens) -> Unit
) {

    var screenToDisplay by rememberSaveable { mutableStateOf(SettingsScreen.SETTINGS) }


    // Mimic back behavior from navigation
    BackHandler {
        if (screenToDisplay != SettingsScreen.SETTINGS) {
            screenToDisplay = SettingsScreen.SETTINGS
        } else {
            onNavigate(Screens.MAIN)
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedFab(
                onClick = {
                    if (screenToDisplay == SettingsScreen.SETTINGS) {
                        onNavigate(Screens.MAIN)
                    } else {
                        screenToDisplay = SettingsScreen.SETTINGS
                    }
                },
                modifier = Modifier
                    .padding(start = 15.dp)
                    .navigationBarsPadding()
                    .selfAlignHorizontally(Alignment.Start),
                icon = R.drawable.back_arrow,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = screenToDisplay,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            transitionSpec = { slideInHorizontally(navigationBouncySpec) { -it } + fadeIn() togetherWith fadeOut() }
        ) { screen ->
            when (screen) {
                SettingsScreen.SETTINGS -> {
                    SettingsPage(
                        onNavigateSettings = { screenToDisplay = it }
                    )
                }

                SettingsScreen.LOOK_AND_FEEL -> { SettingsLookAndFeel() }
                SettingsScreen.HISTORY -> { SettingsHistory() }
                SettingsScreen.FORMATTING -> { SettingsFormatting() }
                SettingsScreen.MISC -> { SettingsMisc() }
                    SettingsScreen.ABOUT -> {
                        SettingsAbout()
                    }
            }
        }
    }
}

@Composable
private fun SettingsPage(
    onNavigateSettings: (SettingsScreen) -> Unit
) {
    val settingsCategories = listOf(
        SettingsCategory(
            name = R.string.look_and_feel,
            description = R.string.look_and_feel_desc,
            icon = R.drawable.palette,
            onNavigate = { onNavigateSettings(SettingsScreen.LOOK_AND_FEEL) }
        ),
        SettingsCategory(
            name = R.string.history,
            description = R.string.history_desc,
            icon = R.drawable.history_rounded,
            onNavigate = { onNavigateSettings(SettingsScreen.HISTORY) }
        ),
        SettingsCategory(
            name = R.string.formatting,
            description = R.string.formatting_desc,
            icon = R.drawable.formatting,
            onNavigate = { onNavigateSettings(SettingsScreen.FORMATTING) }
        ),
        SettingsCategory(
            name = R.string.misc,
            description = R.string.misc_desc,
            icon = R.drawable.more_horiz,
            onNavigate = { onNavigateSettings(SettingsScreen.MISC) }
        )
    )

    Column {
        AboutCard(
            onClick = {
                onNavigateSettings(
                    SettingsScreen.ABOUT
                )
            }
        )
        Spacer(Modifier.height(20.dp))
        settingsCategories.fastForEachIndexed { index, category ->
            SettingsCategoryCard(
                icon = category.icon,
                name = category.name,
                description = category.description,
                topDp = if (index == 0) 24.dp else 4.dp,
                bottomDp = if (index == settingsCategories.lastIndex) 24.dp else 4.dp,
                onNavigate = category.onNavigate
            )
        }
    }

}

@Immutable
private data class SettingsCategory(
    val id: String = Uuid.random().toString(),
    val name: Int,
    val description: Int,
    val icon: Int,
    val onNavigate: () -> Unit
)