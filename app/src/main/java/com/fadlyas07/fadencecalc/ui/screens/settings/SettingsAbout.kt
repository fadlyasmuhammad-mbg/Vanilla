package com.fadlyas07.fadencecalc.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fadlyas07.fadencecalc.R
import com.fadlyas07.fadencecalc.utils.CONTRIBUTORS_PAGE
import com.fadlyas07.fadencecalc.utils.DEVELOPER_PROFILE
import com.fadlyas07.fadencecalc.utils.GITHUB_RELEASES
import com.fadlyas07.fadencecalc.utils.ISSUES_PAGE
import com.fadlyas07.fadencecalc.utils.LICENSE_PAGE
import com.fadlyas07.fadencecalc.utils.PROJECT_SOURCE
import com.fadlyas07.fadencecalc.utils.SUPPORT_PAGE
import com.fadlyas07.fadencecalc.utils.UPSTREAM_PROJECT
import com.fadlyas07.fadencecalc.utils.appVersion
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun SettingsAbout() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
    ) {
        Text(
            text = stringResource(R.string.about),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 8.dp
            )
        )

        AboutHeader(
            version = context.appVersion
        )

        Spacer(Modifier.height(20.dp))

        AboutSectionTitle(
            title = stringResource(R.string.developer)
        )

        AboutLinkGroup(
            items = listOf(
                AboutLinkItem(
                    title = stringResource(
                        R.string.developer_name
                    ),
                    description = stringResource(
                        R.string.developer_description
                    ),
                    url = DEVELOPER_PROFILE
                )
            ),
            onOpen = uriHandler::openUri
        )

        Spacer(Modifier.height(20.dp))

        AboutSectionTitle(
            title = stringResource(R.string.project)
        )

        AboutLinkGroup(
            items = listOf(
                AboutLinkItem(
                    title = stringResource(
                        R.string.source_code
                    ),
                    description = stringResource(
                        R.string.source_code_description
                    ),
                    url = PROJECT_SOURCE
                ),
                AboutLinkItem(
                    title = stringResource(
                        R.string.contributors
                    ),
                    description = stringResource(
                        R.string.contributors_description
                    ),
                    url = CONTRIBUTORS_PAGE
                ),
                AboutLinkItem(
                    title = stringResource(
                        R.string.report_issue
                    ),
                    description = stringResource(
                        R.string.report_issue_description
                    ),
                    url = ISSUES_PAGE
                ),
                AboutLinkItem(
                    title = stringResource(
                        R.string.check_updates
                    ),
                    description = stringResource(
                        R.string.check_updates_description
                    ),
                    url = GITHUB_RELEASES
                )
            ),
            onOpen = uriHandler::openUri
        )

        Spacer(Modifier.height(20.dp))

        AboutSectionTitle(
            title = stringResource(R.string.support)
        )

        AboutLinkGroup(
            items = listOf(
                AboutLinkItem(
                    title = stringResource(
                        R.string.support_developer
                    ),
                    description = stringResource(
                        R.string.support_developer_description
                    ),
                    url = SUPPORT_PAGE
                )
            ),
            onOpen = uriHandler::openUri
        )

        Spacer(Modifier.height(20.dp))

        AboutSectionTitle(
            title = stringResource(R.string.open_source)
        )

        AboutLinkGroup(
            items = listOf(
                AboutLinkItem(
                    title = stringResource(
                        R.string.original_project
                    ),
                    description = stringResource(
                        R.string.original_project_description
                    ),
                    url = UPSTREAM_PROJECT
                ),
                AboutLinkItem(
                    title = stringResource(
                        R.string.license_name
                    ),
                    description = stringResource(
                        R.string.license_description
                    ),
                    url = LICENSE_PAGE
                )
            ),
            onOpen = uriHandler::openUri
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AboutHeader(
    version: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .background(
                        shape = SquircleShape(
                            smoothing = CornerSmoothing.Full
                        ),
                        color = Color(0xFFF4A7BD)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.calculator
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = Color(0xFFFDD9DC)
                )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${stringResource(R.string.version)} " +
                        version,
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = stringResource(
                        R.string.about_short_description
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AboutSectionTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            horizontal = 8.dp,
            vertical = 8.dp
        )
    )
}

@Composable
private fun AboutLinkGroup(
    items: List<AboutLinkItem>,
    onOpen: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOpen(item.url)
                    }
                    .padding(
                        horizontal = 18.dp,
                        vertical = 15.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.title,
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = item.description,
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                Text(
                    text = "›",
                    style =
                        MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (index != items.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 18.dp
                    ),
                    color =
                        MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

private data class AboutLinkItem(
    val title: String,
    val description: String,
    val url: String
)
