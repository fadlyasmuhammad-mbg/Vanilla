package com.fadlyas07.fadencecalc

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.fadlyas07.fadencecalc.data.datastore.rememberAppTheme
import com.fadlyas07.fadencecalc.data.datastore.rememberShowOnLockScreen
import com.fadlyas07.fadencecalc.ui.navigation.Nav
import com.fadlyas07.fadencecalc.ui.theme.FadenceCalcTheme
import com.fadlyas07.fadencecalc.utils.CuteTheme
import com.fadlyas07.fadencecalc.utils.showOnLockScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            val systemDarkMode = isSystemInDarkTheme()
            val theme by rememberAppTheme()
            val showOnLockScreen by rememberShowOnLockScreen()

            showOnLockScreen(showOnLockScreen)

            FadenceCalcTheme {
                val useLightSystemIcons = when (theme) {
                    CuteTheme.LIGHT -> true
                    CuteTheme.SYSTEM -> !systemDarkMode
                    else -> false
                }

                val systemBarColor =
                    MaterialTheme.colorScheme.background.toArgb()

                SideEffect {
                    val controller = WindowCompat.getInsetsController(
                        window,
                        window.decorView
                    )

                    controller.isAppearanceLightStatusBars =
                        useLightSystemIcons

                    controller.isAppearanceLightNavigationBars =
                        useLightSystemIcons

                    window.navigationBarColor = systemBarColor

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        window.isNavigationBarContrastEnforced = false
                    }
                }

                Nav()
            }
        }
    }
}
