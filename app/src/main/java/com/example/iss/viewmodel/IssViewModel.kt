package com.example.iss.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iss.data.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IssUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val hasLocationPermission: Boolean = false
)

class IssViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IssUiState())
    val uiState: StateFlow<IssUiState> = _uiState.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    init {
        fetchIssPosition()
    }

    fun initLocation(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val client = fusedLocationClient ?: return
        _uiState.value = _uiState.value.copy(hasLocationPermission = true)

        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(3000L)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location: Location = result.lastLocation ?: return
                _uiState.value = _uiState.value.copy(
                    userLatitude = location.latitude,
                    userLongitude = location.longitude
                )
            }
        }

        client.requestLocationUpdates(request, locationCallback!!, Looper.getMainLooper())

        client.lastLocation.addOnSuccessListener { location ->
            if (location != null && _uiState.value.userLatitude == null) {
                _uiState.value = _uiState.value.copy(
                    userLatitude = location.latitude,
                    userLongitude = location.longitude
                )
            }
        }
    }

    fun onLocationPermissionDenied() {
        _uiState.value = _uiState.value.copy(hasLocationPermission = false)
    }

    private fun fetchIssPosition() {
        viewModelScope.launch {
            while (true) {
                try {
                    val response = RetrofitClient.apiService.getIssPosition()
                    _uiState.value = _uiState.value.copy(
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

    override fun onCleared() {
        super.onCleared()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }
}
