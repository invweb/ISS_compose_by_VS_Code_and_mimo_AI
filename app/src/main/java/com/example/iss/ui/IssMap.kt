package com.example.iss.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.iss.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun IssMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val markerTitle = stringResource(R.string.marker_title)
    val markerSnippet = stringResource(
        R.string.marker_snippet,
        String.format("%.4f", latitude),
        String.format("%.4f", longitude)
    )

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

    LaunchedEffect(latitude, longitude) {
        mapView.overlays.clear()
        val issPoint = GeoPoint(latitude, longitude)
        val marker = Marker(mapView).apply {
            position = issPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = markerTitle
            snippet = markerSnippet
            icon = BitmapDrawable(context.resources, drawableToBitmap(context, R.drawable.ic_iss, 80))
        }
        mapView.overlays.add(marker)
        mapView.controller.animateTo(issPoint)
        mapView.invalidate()
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )
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
