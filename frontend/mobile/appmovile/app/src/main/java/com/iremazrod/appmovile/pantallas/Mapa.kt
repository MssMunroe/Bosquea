package com.iremazrod.appmovile.pantallas

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.iremazrod.appmovile.data.network.ParqueResponse
import com.iremazrod.appmovile.data.network.RetrofitClient
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun Mapa(navController: NavController) {
    var listaParques by remember { mutableStateOf<List<ParqueResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.instance.getParques()
            listaParques = response
        } catch (e: Exception) {
            Log.e("OSM_ERROR", "${e.message}")
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(6.0)
                    controller.setCenter(GeoPoint(40.4167, -3.7038))
                }
            },
            update = { mapView ->
                // Añadimos los marcadores
                mapView.overlays.clear()
                listaParques.forEach { parque ->
                    val lat = parque.lat.toDoubleOrNull()
                    val lon = parque.lon.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        val marker = Marker(mapView)
                        marker.position = GeoPoint(lat, lon)
                        marker.title = parque.nombre
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.setOnMarkerClickListener { _, _ ->
                            navController.navigate("parque/${parque.nombre}")
                            true
                        }
                        mapView.overlays.add(marker)
                    }
                }
                mapView.invalidate() // Refrescar el mapa
            }
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}