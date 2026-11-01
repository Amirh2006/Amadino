package com.amadino.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.amadino.app.di.AppContainer
import com.amadino.app.ui.navigation.AmadinoNavGraph
import com.amadino.app.ui.theme.AmadinoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = AppContainer.getInstance(applicationContext)

        setContent {
            AmadinoTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AmadinoNavGraph(container = container)
                }
            }
        }
    }
}
