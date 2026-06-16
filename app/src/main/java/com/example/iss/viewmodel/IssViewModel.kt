package com.example.iss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iss.data.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IssUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class IssViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IssUiState())
    val uiState: StateFlow<IssUiState> = _uiState.asStateFlow()

    init {
        fetchIssPosition()
    }

    private fun fetchIssPosition() {
        viewModelScope.launch {
            while (true) {
                try {
                    val response = RetrofitClient.apiService.getIssPosition()
                    _uiState.value = IssUiState(
                        latitude = response.issPosition.latitudeDouble(),
                        longitude = response.issPosition.longitudeDouble(),
                        isLoading = false
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
                delay(5000)
            }
        }
    }
}
