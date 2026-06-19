package com.example.iss.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.iss.R

@Composable
fun MenuScreen(onMap: () -> Unit, onSettings: () -> Unit, onAstro: () -> Unit, onExit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = stringResource(R.string.menu_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.ic_iss),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onMap,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.menu_map))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onAstro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.menu_astro))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.menu_settings))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.menu_exit))
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
