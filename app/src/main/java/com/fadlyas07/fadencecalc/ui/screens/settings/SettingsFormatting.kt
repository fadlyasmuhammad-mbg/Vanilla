@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.fadlyas07.fadencecalc.ui.screens.settings

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.fadlyas07.fadencecalc.R
import com.fadlyas07.fadencecalc.data.datastore.rememberDecimal
import com.fadlyas07.fadencecalc.data.datastore.rememberDecimalPrecision
import com.fadlyas07.fadencecalc.ui.screens.settings.components.SettingsDropdownMenu
import com.fadlyas07.fadencecalc.ui.screens.settings.components.SettingsSwitch
import com.fadlyas07.fadencecalc.ui.screens.settings.components.SettingsWithTitle

@Composable
fun SettingsFormatting() {
    var shouldFormat by rememberDecimal()
    var decimalPrecision by rememberDecimalPrecision()

    val standardPrecisionOptions = listOf(
        2,
        4,
        6,
        8,
        10,
        12,
        15,
        1000
    )

    /*
     * Preserve an existing value such as 14 until the user
     * selects one of the new standard options.
     */
    val decimalPrecisionOptions = remember(decimalPrecision) {
        if (decimalPrecision in standardPrecisionOptions) {
            standardPrecisionOptions
        } else {
            (standardPrecisionOptions + decimalPrecision)
                .distinct()
                .sortedWith(
                    compareBy {
                        if (it == 1000) {
                            Int.MAX_VALUE
                        } else {
                            it
                        }
                    }
                )
        }
    }

    SettingsWithTitle(
        title = R.string.formatting
    ) {
        SettingsSwitch(
            checked = shouldFormat,
            onCheckedChange = {
                shouldFormat = !shouldFormat
            },
            topDp = 24.dp,
            bottomDp = 4.dp,
            text = R.string.decimal_formatting
        )

        SettingsDropdownMenu(
            value = decimalPrecision.toLong(),
            topDp = 4.dp,
            bottomDp = 24.dp,
            text = R.string.decimal_precision,
            optionalDescription =
                R.string.decimal_precision_desc
        ) {
            decimalPrecisionOptions.fastForEachIndexed {
                    index,
                    number ->

                val selected =
                    number == decimalPrecision

                DropdownMenuItem(
                    onClick = {
                        decimalPrecision = number
                    },
                    selected = selected,
                    text = {
                        Text(
                            text = if (number == 1000) {
                                stringResource(
                                    R.string.no_limit
                                )
                            } else {
                                number.toString()
                            }
                        )
                    },
                    shapes = MenuDefaults.itemShape(
                        index,
                        decimalPrecisionOptions.size
                    ),
                    trailingIcon = {
                        if (selected) {
                            Icon(
                                painter = painterResource(
                                    R.drawable.check
                                ),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}
