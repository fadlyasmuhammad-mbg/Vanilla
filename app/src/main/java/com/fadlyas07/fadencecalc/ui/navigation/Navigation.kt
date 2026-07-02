package com.fadlyas07.fadencecalc.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fadlyas07.fadencecalc.data.actions.CalcAction
import com.fadlyas07.fadencecalc.data.datastore.rememberIsLandscape
import com.fadlyas07.fadencecalc.ui.screens.calculator.CalculatorScreen
import com.fadlyas07.fadencecalc.ui.screens.calculator.CalculatorScreenLandscape
import com.fadlyas07.fadencecalc.ui.screens.calculator.CalculatorViewModel
import com.fadlyas07.fadencecalc.ui.screens.history.HistoryScreen
import com.fadlyas07.fadencecalc.ui.screens.history.HistoryViewModel
import com.fadlyas07.fadencecalc.ui.screens.settings.SettingsScreen
import com.fadlyas07.fadencecalc.utils.CalculatorViewModelFactory
import com.fadlyas07.fadencecalc.utils.HistoryViewModelFactory
import com.fadlyas07.fadencecalc.utils.bouncySpec
import com.fadlyas07.fadencecalc.utils.navigationBouncySpec
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Nav() {


    val activity = LocalActivity.current!!
    val isLandscape = rememberIsLandscape()
    val viewModel =
        viewModel<CalculatorViewModel>(factory = CalculatorViewModelFactory(activity.application))
    val historyViewModel =
        viewModel<HistoryViewModel>(factory = HistoryViewModelFactory(activity.application))
    var screenToDisplay by rememberSaveable { mutableStateOf(Screens.MAIN) }

    val windowInfo = LocalWindowInfo.current

    // Mimic back behavior from navigation
    BackHandler {
        if (screenToDisplay != Screens.MAIN) {
            screenToDisplay = Screens.MAIN
        } else {
            activity.moveTaskToBack(true)
        }
    }

    AnimatedContent(
        targetState = screenToDisplay,
        transitionSpec = { slideInHorizontally(navigationBouncySpec) { -it } + fadeIn() togetherWith fadeOut() },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) { screen ->
        when (screen) {
            Screens.MAIN -> {
                // survive config changes without needing a saver
                val yTranslation = retain { Animatable(0f) }
                val scope = rememberCoroutineScope()

                Box(
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                ) {
                    val calculations by historyViewModel.allCalculations.collectAsStateWithLifecycle()
                    HistoryScreen(
                        calculations = calculations,
                        onEvents = historyViewModel::onEvent,
                        onPutBackToField = { expression ->
                            viewModel.handleAction(CalcAction.AddExpressionToField(expression))
                        },
                        onGotoMain = {
                            scope.launch {
                                yTranslation.animateTo(0f, bouncySpec())
                            }
                        }
                    )

                    if (isLandscape) {
                        CalculatorScreenLandscape(
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = yTranslation.value
                                },
                            viewModel = viewModel,
                            historyViewModel = historyViewModel,
                            onNavigate = { screenToDisplay = it },
                            onGotoHistory = {
                                scope.launch {
                                    yTranslation.animateTo(windowInfo.containerSize.height.toFloat(), bouncySpec())
                                }
                            }
                        )
                    } else {
                        CalculatorScreen(
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = yTranslation.value
                                },
                            viewModel = viewModel,
                            onNavigate = { screenToDisplay = it },
                            historyViewModel = historyViewModel,
                            onUpdateDragAmount = { dragAmount ->
                                val value = (yTranslation.value + dragAmount).coerceAtLeast(0f) // always keep the value positive or else it's a shithole to manage

                                scope.launch {
                                    yTranslation.snapTo(value)
                                }
                            },
                            onDragStopped = {
                                if (yTranslation.value.roundToInt() >= windowInfo.containerSize.height / 2) {
                                    yTranslation.animateTo(windowInfo.containerSize.height.toFloat(), bouncySpec())
                                } else {
                                    yTranslation.animateTo(0f, bouncySpec())
                                }
                            }
                        )
                    }
                }
            }

            Screens.SETTINGS -> {
                SettingsScreen(
                    onNavigate = { screenToDisplay = it }
                )
            }
        }
    }

}