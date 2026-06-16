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
import com.example.iss.ui.IssScreen
import com.example.iss.ui.MenuScreen
import com.example.iss.ui.SplashScreen
import com.example.iss.ui.theme.ISSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISSTheme {
                var screen by remember { mutableStateOf("splash") }
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    when (screen) {
                        "splash" -> SplashScreen(onFinished = { screen = "menu" })
                        "menu" -> MenuScreen(
                            onMap = { screen = "map" },
                            onExit = { finish() }
                        )
                        "map" -> IssScreen(onBack = { screen = "menu" })
                    }
                }
            }
        }
    }
}
