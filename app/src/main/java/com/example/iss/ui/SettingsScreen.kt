package com.example.iss.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.iss.R

private const val PREFS_NAME = "iss_settings"
private const val KEY_ISS_COLOR = "iss_marker_color"
private const val KEY_PATH_COLOR = "iss_path_color"
const val DEFAULT_ISS_COLOR = 0xFF333333.toInt()
const val DEFAULT_PATH_COLOR = 0xFFE53935.toInt()

private val colorOptions = listOf(
    0xFFE53935.toInt() to "Red",
    0xFF1E88E5.toInt() to "Blue",
    0xFF43A047.toInt() to "Green",
    0xFFFF9800.toInt() to "Orange",
    0xFFFFEB3B.toInt() to "Yellow",
    0xFF8E24AA.toInt() to "Purple",
    0xFF00BCD4.toInt() to "Cyan",
    0xFF333333.toInt() to "Dark",
    0xFFFFFFFF.toInt() to "White",
    0xFFE91E63.toInt() to "Pink"
)

fun getIssMarkerColor(context: Context): Int {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getInt(KEY_ISS_COLOR, DEFAULT_ISS_COLOR)
}

fun getPathColor(context: Context): Int {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getInt(KEY_PATH_COLOR, DEFAULT_PATH_COLOR)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val pathPrefs = remember { context.getSharedPreferences("iss_path", Context.MODE_PRIVATE) }
    var showClearDialog by remember { mutableStateOf(false) }
    val currentIssColor = remember { getIssMarkerColor(context) }
    val currentPathColor = remember { getPathColor(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_iss_color),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorOptions.forEach { (colorInt, _) ->
                    val isSelected = colorInt == currentIssColor
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(colorInt))
                            .then(
                                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier.border(1.dp, Color.Gray, CircleShape)
                            )
                            .clickable {
                                prefs.edit().putInt(KEY_ISS_COLOR, colorInt).apply()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.settings_path_color),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                colorOptions.forEach { (colorInt, _) ->
                    val isSelected = colorInt == currentPathColor
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(colorInt))
                            .then(
                                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier.border(1.dp, Color.Gray, CircleShape)
                            )
                            .clickable {
                                prefs.edit().putInt(KEY_PATH_COLOR, colorInt).apply()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.settings_clear_path))
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            confirmButton = {
                Button(onClick = {
                    pathPrefs.edit().remove("path_history").apply()
                    showClearDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.back))
                }
            },
            title = { Text(stringResource(R.string.settings_clear_path)) }
        )
    }
}
