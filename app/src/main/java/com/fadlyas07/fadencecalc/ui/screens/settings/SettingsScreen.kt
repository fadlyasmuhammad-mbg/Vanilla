@file:OptIn(ExperimentalMaterial3Api::class)

package com.fadlyas07.fadencecalc.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fadlyas07.fadencecalc.R
import com.fadlyas07.fadencecalc.ui.navigation.Screens
import com.fadlyas07.fadencecalc.ui.navigation.SettingsScreen
import com.fadlyas07.fadencecalc.utils.navigationBouncySpec

@Composable
fun SettingsScreen(
    onNavigate: (Screens) -> Unit
) {
    var screenToDisplay by rememberSaveable {
        mutableStateOf(SettingsScreen.SETTINGS)
    }

    fun navigateBack() {
        if (screenToDisplay == SettingsScreen.SETTINGS) {
            onNavigate(Screens.MAIN)
        } else {
            screenToDisplay = SettingsScreen.SETTINGS
        }
    }

    BackHandler {
        navigateBack()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            SettingsTopBar(
                onBack = ::navigateBack
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = screenToDisplay,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                ),
            transitionSpec = {
                slideInHorizontally(
                    navigationBouncySpec
                ) { -it } + fadeIn() togetherWith fadeOut()
            }
        ) { screen ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(bottom = 24.dp)
            ) {
                when (screen) {
                    SettingsScreen.SETTINGS -> {
                        SettingsPage(
                            onNavigateSettings = {
                                screenToDisplay = it
                            }
                        )
                    }

                    SettingsScreen.LOOK_AND_FEEL -> {
                        SettingsLookAndFeel()
                    }

                    SettingsScreen.HISTORY -> {
                        SettingsHistory()
                    }

                    SettingsScreen.FORMATTING -> {
                        SettingsFormatting()
                    }

                    SettingsScreen.MISC -> {
                        SettingsMisc()
                    }

                    SettingsScreen.ABOUT -> {
                        SettingsAbout()
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.back_arrow
                    ),
                    contentDescription = stringResource(
                        R.string.back
                    )
                )
            }
        },
        title = {
            Text(
                text = stringResource(
                    R.string.settings
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
                MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun SettingsPage(
    onNavigateSettings: (SettingsScreen) -> Unit
) {
    val preferences = listOf(
        SettingsEntry(
            title = R.string.look_and_feel,
            description = R.string.look_and_feel_desc,
            icon = R.drawable.palette,
            onClick = {
                onNavigateSettings(
                    SettingsScreen.LOOK_AND_FEEL
                )
            }
        ),
        SettingsEntry(
            title = R.string.history,
            description = R.string.history_desc,
            icon = R.drawable.history_rounded,
            onClick = {
                onNavigateSettings(
                    SettingsScreen.HISTORY
                )
            }
        ),
        SettingsEntry(
            title = R.string.formatting,
            description = R.string.formatting_desc,
            icon = R.drawable.formatting,
            onClick = {
                onNavigateSettings(
                    SettingsScreen.FORMATTING
                )
            }
        ),
        SettingsEntry(
            title = R.string.misc,
            description = R.string.misc_desc,
            icon = R.drawable.more_horiz,
            onClick = {
                onNavigateSettings(
                    SettingsScreen.MISC
                )
            }
        )
    )

    val about = listOf(
        SettingsEntry(
            title = R.string.about_fadence_calc,
            description = R.string.about_short_description,
            icon = R.drawable.calculator,
            onClick = {
                onNavigateSettings(
                    SettingsScreen.ABOUT
                )
            }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {
        SettingsSectionTitle(
            title = stringResource(
                R.string.preferences
            )
        )

        SettingsGroup(
            entries = preferences
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        SettingsSectionTitle(
            title = stringResource(
                R.string.about
            )
        )

        SettingsGroup(
            entries = about
        )
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            horizontal = 8.dp,
            vertical = 10.dp
        )
    )
}

@Composable
private fun SettingsGroup(
    entries: List<SettingsEntry>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        entries.forEachIndexed { index, entry ->
            SettingsRow(
                entry = entry
            )

            if (index != entries.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        start = 76.dp,
                        end = 18.dp
                    ),
                    color =
                        MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    entry: SettingsEntry
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = entry.onClick
            )
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color =
                        MaterialTheme.colorScheme
                            .secondaryContainer,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    entry.icon
                ),
                contentDescription = null,
                tint =
                    MaterialTheme.colorScheme
                        .onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            verticalArrangement =
                Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(
                    entry.title
                ),
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(
                    entry.description
                ),
                style =
                    MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }

        Text(
            text = "›",
            style =
                MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Immutable
private data class SettingsEntry(
    val title: Int,
    val description: Int,
    val icon: Int,
    val onClick: () -> Unit
)
