@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.vanilla.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.sosauce.vanilla.R
import com.sosauce.vanilla.data.datastore.rememberDecimal
import com.sosauce.vanilla.data.datastore.rememberDecimalPrecision
import com.sosauce.vanilla.ui.screens.settings.components.SettingsDropdownMenu
import com.sosauce.vanilla.ui.screens.settings.components.SettingsSwitch
import com.sosauce.vanilla.ui.screens.settings.components.SettingsWithTitle
import com.sosauce.vanilla.ui.shared_components.AnimatedFab
import com.sosauce.vanilla.utils.formatNumber
import com.sosauce.vanilla.utils.selfAlignHorizontally

@Composable
fun SettingsFormatting() {
    var shouldFormat by rememberDecimal()
    var decimalPrecision by rememberDecimalPrecision()
    val decimalPrecisionOptions = MutableList(16) { it }.apply { add(1000) }

    Column {
        SettingsWithTitle(
            title = R.string.formatting
        ) {
            SettingsSwitch(
                checked = shouldFormat,
                onCheckedChange = { shouldFormat = !shouldFormat },
                topDp = 24.dp,
                bottomDp = 4.dp,
                text = R.string.decimal_formatting
            )
            SettingsDropdownMenu(
                value = decimalPrecision.toLong(),
                topDp = 4.dp,
                bottomDp = 24.dp,
                text = R.string.decimal_precision,
                optionalDescription = R.string.decimal_precision_desc
            ) {
                decimalPrecisionOptions.fastForEachIndexed { index, number ->

                    val selected = number == decimalPrecision

                    DropdownMenuItem(
                        onClick = { decimalPrecision = number },
                        selected = selected,
                        text = { Text(number.toString().formatNumber(shouldFormat)) },
                        shapes = MenuDefaults.itemShape(index, decimalPrecisionOptions.count()),
                        trailingIcon = {
                            if (selected) {
                                Icon(
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}