package com.example.iss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.iss.ui.AstronautDetailScreen
import com.example.iss.ui.AstroScreen
import com.example.iss.ui.IssScreen
import com.example.iss.ui.MenuScreen
import com.example.iss.ui.SettingsScreen
import com.example.iss.ui.SplashScreen
import com.example.iss.ui.theme.ISSTheme

/**
 * Определяет доступные экраны приложения.
 * Использование sealed class обеспечивает безопасность типов и исключает ошибки при вводе строк.
 */
sealed class Screen {
    object Splash : Screen()
    object Menu : Screen()
    object Map : Screen()
    object Settings : Screen()
    object Astro : Screen()
    data class Detail(val name: String, val craft: String) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISSTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                var selectedName by remember { mutableStateOf("") }
                var selectedCraft by remember { mutableStateOf("") }
                
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        is Screen.Splash ->
                            SplashScreen(onFinished = { currentScreen = Screen.Menu })
                        is Screen.Menu ->
                            MenuScreen(
                                onMap = { currentScreen = Screen.Map },
                                onSettings = { currentScreen = Screen.Settings },
                                onAstro = { currentScreen = Screen.Astro },
                                onExit = { finish() }
                            )
                        is Screen.Map ->
                            IssScreen(onBack = { currentScreen = Screen.Menu })
                        is Screen.Settings -> SettingsScreen(onBack = { currentScreen = Screen.Menu })
                        is Screen.Astro ->
                            AstroScreen(
                                onBack = { currentScreen = Screen.Menu },
                                onPersonClick = { name, craft ->
                                    selectedName = name
                                    selectedCraft = craft
                                    currentScreen = Screen.Detail(name, craft)
                                }
                            )
                        is Screen.Detail ->
                            AstronautDetailScreen(
                                name = selectedName,
                                craft = selectedCraft,
                                onBack = { currentScreen = Screen.Astro }
                            )
                    }
                }
            }
        }
    }
}
