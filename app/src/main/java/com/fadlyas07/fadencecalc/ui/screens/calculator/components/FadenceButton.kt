@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.fadlyas07.fadencecalc.ui.screens.calculator.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fadlyas07.fadencecalc.R
import com.fadlyas07.fadencecalc.data.datastore.rememberIsLandscape
import com.fadlyas07.fadencecalc.data.datastore.rememberUseButtonsAnimation
import com.fadlyas07.fadencecalc.data.datastore.rememberVibration
import com.fadlyas07.fadencecalc.utils.BACKSPACE
import com.fadlyas07.fadencecalc.utils.PARENTHESES

@Composable
fun FadenceButton(
    modifier: Modifier = Modifier,
    text: String,
    buttonType: ButtonType = ButtonType.OTHER,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource =
        remember { MutableInteractionSource() },
    rectangle: Boolean
) {
    val haptic = LocalHapticFeedback.current
    val shouldVibrate by rememberVibration()
    val useButtonsAnimation by rememberUseButtonsAnimation()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isLandscape = rememberIsLandscape()

    val restingCorner = if (isLandscape) {
        16.dp
    } else {
        24.dp
    }

    val pressedCorner = if (isLandscape) {
        11.dp
    } else {
        17.dp
    }

    val cornerRadius by animateDpAsState(
        targetValue = if (
            isPressed && useButtonsAnimation
        ) {
            pressedCorner
        } else {
            restingCorner
        },
        label = "fadenceButtonCorner"
    )

    val backgroundColor = when (buttonType) {
        ButtonType.OPERATOR ->
            MaterialTheme.colorScheme.primary

        ButtonType.ACTION ->
            MaterialTheme.colorScheme.primaryContainer

        ButtonType.SPECIAL ->
            MaterialTheme.colorScheme.secondaryContainer

        ButtonType.OTHER ->
            MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val foregroundColor = when (buttonType) {
        ButtonType.OPERATOR ->
            MaterialTheme.colorScheme.onPrimary

        ButtonType.ACTION ->
            MaterialTheme.colorScheme.onPrimaryContainer

        ButtonType.SPECIAL ->
            MaterialTheme.colorScheme.onSecondaryContainer

        ButtonType.OTHER ->
            MaterialTheme.colorScheme.onSurface
    }

    val iconSize = if (isLandscape) {
        26.dp
    } else {
        30.dp
    }

    val textStyle = if (isLandscape) {
        MaterialTheme.typography.headlineMedium
    } else {
        MaterialTheme.typography.headlineLarge
    }

    Box(
        modifier = modifier
            .semantics {
                role = Role.Button
            }
            .clip(
                RoundedCornerShape(cornerRadius)
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = {
                    onClick()

                    if (shouldVibrate) {
                        haptic.performHapticFeedback(
                            HapticFeedbackType.Confirm
                        )
                    }
                },
                onLongClick = onLongClick
            )
            .defaultMinSize(
                minWidth = ButtonDefaults.MinWidth,
                minHeight = ButtonDefaults.MinHeight
            )
            .background(backgroundColor)
            .then(
                if (!isLandscape && !rectangle) {
                    Modifier.aspectRatio(1f)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (text) {
            BACKSPACE -> {
                Icon(
                    painter = painterResource(
                        R.drawable.backspace_filled
                    ),
                    contentDescription = stringResource(
                        R.string.back
                    ),
                    tint = foregroundColor,
                    modifier = Modifier.size(iconSize)
                )
            }

            PARENTHESES -> {
                Icon(
                    painter = painterResource(
                        R.drawable.parentheses
                    ),
                    contentDescription = null,
                    tint = foregroundColor,
                    modifier = Modifier.size(iconSize)
                )
            }

            else -> {
                Text(
                    text = text,
                    color = foregroundColor,
                    style = textStyle,
                    fontWeight = when (buttonType) {
                        ButtonType.OTHER ->
                            FontWeight.SemiBold

                        else ->
                            FontWeight.Bold
                    }
                )
            }
        }
    }
}

enum class ButtonType {
    OPERATOR,
    SPECIAL,
    ACTION,
    OTHER
}
