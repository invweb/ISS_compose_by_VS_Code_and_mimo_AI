package com.example.iss.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.iss.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class Landmark(val nameResId: Int, val lat: Double, val lng: Double)

private val landmarks = listOf(
    Landmark(R.string.landmark_eiffel, 48.8584, 2.2945),
    Landmark(R.string.landmark_great_wall, 40.4319, 116.5704),
    Landmark(R.string.landmark_statue_liberty, 40.6892, -74.0445),
    Landmark(R.string.landmark_pyramids, 29.9792, 31.1342),
    Landmark(R.string.landmark_taj_mahal, 27.1751, 78.0421),
    Landmark(R.string.landmark_colosseum, 41.8902, 12.4922),
    Landmark(R.string.landmark_petra, 30.3285, 35.4444),
    Landmark(R.string.landmark_machu_picchu, -13.1631, -72.5450),
    Landmark(R.string.landmark_christ, -22.9519, -43.2105),
    Landmark(R.string.landmark_tower_pisa, 43.7230, 10.3966),
    Landmark(R.string.landmark_big_ben, 51.5007, -0.1246),
    Landmark(R.string.landmark_sydney_opera, -33.8568, 151.2153),
    Landmark(R.string.landmark_kremlin, 55.7520, 37.6175),
    Landmark(R.string.landmark_golden_gate, 37.8199, -122.4783),
    Landmark(R.string.landmark_angkor_wat, 13.4125, 103.8670),
    Landmark(R.string.landmark_mount_fuji, 35.3606, 138.7274),
    Landmark(R.string.landmark_sagrada_familia, 41.4036, 2.1744),
    Landmark(R.string.landmark_parthenon, 37.9715, 23.7267),
    Landmark(R.string.landmark_marina_bay, 1.2834, 103.8607),
    Landmark(R.string.landmark_burj_khalifa, 25.1972, 55.2744)
)

@Composable
fun IssMap(
    latitude: Double,
    longitude: Double,
    userLatitude: Double? = null,
    userLongitude: Double? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val markerTitle = stringResource(R.string.marker_title)
    val markerSnippet = stringResource(
        R.string.marker_snippet,
        String.format("%.4f", latitude),
        String.format("%.4f", longitude)
    )
    val myLocationTitle = stringResource(R.string.my_location)

    Configuration.getInstance()
        .load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    Configuration.getInstance().userAgentValue = context.packageName

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(3.0)
            controller.setCenter(GeoPoint(latitude, longitude))
        }
    }

    LaunchedEffect(latitude, longitude, userLatitude, userLongitude) {
        mapView.overlays.clear()

        val issPoint = GeoPoint(latitude, longitude)
        val issMarker = Marker(mapView).apply {
            position = issPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = markerTitle
            snippet = markerSnippet
            icon = BitmapDrawable(context.resources, drawableToBitmap(context, R.drawable.ic_iss, 80))
        }
        mapView.overlays.add(issMarker)

        if (userLatitude != null && userLongitude != null) {
            val userPoint = GeoPoint(userLatitude, userLongitude)
            val userMarker = Marker(mapView).apply {
                position = userPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = myLocationTitle
                icon = BitmapDrawable(context.resources, drawableToBitmap(context, R.drawable.ic_person, 50))
            }
            mapView.overlays.add(userMarker)
        }

        for (landmark in landmarks) {
            val point = GeoPoint(landmark.lat, landmark.lng)
            val marker = Marker(mapView).apply {
                position = point
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = context.getString(landmark.nameResId)
                snippet = String.format("%.4f, %.4f", landmark.lat, landmark.lng)
                icon = BitmapDrawable(context.resources, drawableToBitmap(context, R.drawable.ic_landmark, 40))
            }
            mapView.overlays.add(marker)
        }

        mapView.controller.animateTo(issPoint)
        mapView.invalidate()
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        FloatingActionButton(
            onClick = {
                val uLat = userLatitude
                val uLng = userLongitude
                if (uLat != null && uLng != null) {
                    mapView.controller.animateTo(GeoPoint(uLat, uLng))
                    mapView.controller.setZoom(12.0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = stringResource(R.string.my_location)
            )
        }
    }
}

private fun drawableToBitmap(context: Context, resId: Int, sizeDp: Int): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    val drawable: Drawable = androidx.core.content.ContextCompat.getDrawable(context, resId)!!
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, sizePx, sizePx)
    drawable.draw(canvas)
    return bitmap
}
