package com.fadlyas07.fadencecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
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
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val theme by rememberAppTheme()
            val showOnLockScreen by rememberShowOnLockScreen()

            showOnLockScreen(showOnLockScreen)

            FadenceCalcTheme {
                WindowCompat
                    .getInsetsController(window, window.decorView)
                    .apply {

                        val isLight =
                            if (theme == CuteTheme.SYSTEM) !isSystemInDarkTheme else theme == CuteTheme.LIGHT

                        isAppearanceLightStatusBars = isLight
                        isAppearanceLightNavigationBars = isLight
                    }

                Nav()
            }
        }
    }
}

