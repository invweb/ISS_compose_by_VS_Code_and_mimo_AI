package com.example.iss.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iss.data.AstroPerson
import com.example.iss.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AstroUiState(
    val people: List<AstroPerson> = emptyList(),
    val totalNumber: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class AstroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AstroUiState())
    val uiState: StateFlow<AstroUiState> = _uiState.asStateFlow()

    init {
        fetchAstroPeople()
    }

    private fun fetchAstroPeople() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAstroPeople()
                _uiState.value = AstroUiState(
                    people = response.people,
                    totalNumber = response.number,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = AstroUiState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
