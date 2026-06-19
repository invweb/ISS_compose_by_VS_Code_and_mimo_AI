package com.example.iss.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import org.json.JSONArray

data class IssUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val hasLocationPermission: Boolean = false,
    val pathHistory: List<Pair<Double, Double>> = emptyList()
)

private const val PREFS_NAME = "iss_path"
private const val KEY_PATH = "path_history"

class IssViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IssUiState())
    val uiState: StateFlow<IssUiState> = _uiState.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var prefs: SharedPreferences? = null

    fun initPrefs(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _uiState.value = _uiState.value.copy(pathHistory = loadPath())
    }

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
                    val lat = response.issPosition.latitudeDouble()
                    val lng = response.issPosition.longitudeDouble()
                    val history = _uiState.value.pathHistory + Pair(lat, lng)
                    val trimmed = if (history.size > 200) history.takeLast(200) else history
                    _uiState.value = _uiState.value.copy(
                        latitude = lat,
                        longitude = lng,
                        isLoading = false,
                        error = null,
                        pathHistory = trimmed
                    )
                    savePath(trimmed)
                } catch (e: Exception) {
                    if (_uiState.value.latitude == 0.0 && _uiState.value.longitude == 0.0) {
                        _uiState.value = _uiState.value.copy(
                            error = e.message,
                            isLoading = false
                        )
                    }
                }
                delay(5000)
            }
        }
    }

    private fun savePath(path: List<Pair<Double, Double>>) {
        val array = JSONArray()
        path.forEach { (lat, lng) ->
            val obj = org.json.JSONObject()
            obj.put("lat", lat)
            obj.put("lng", lng)
            array.put(obj)
        }
        prefs?.edit()?.putString(KEY_PATH, array.toString())?.apply()
    }

    private fun loadPath(): List<Pair<Double, Double>> {
        val json = prefs?.getString(KEY_PATH, null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Pair(obj.getDouble("lat"), obj.getDouble("lng"))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }
}
