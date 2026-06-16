package com.example.iss.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iss.R
import com.example.iss.viewmodel.IssViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssScreen(
    onBack: () -> Unit,
    viewModel: IssViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.toolbar_map)) },
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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(top = 8.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.iss_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else if (uiState.error != null) {
                        Text(
                            text = stringResource(R.string.error, uiState.error!!),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.latitude, String.format("%.4f", uiState.latitude)),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.longitude, String.format("%.4f", uiState.longitude))
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth().clipToBounds()) {
                if (!uiState.isLoading) {
                    IssMap(
                        latitude = uiState.latitude,
                        longitude = uiState.longitude,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
