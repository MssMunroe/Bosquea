package com.iremazrod.appmovile.pantallas

import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.iremazrod.appmovile.ui.theme.Screens


@Composable
fun Mapa(navController: NavController) {
    val htmlMapa = """
        <!DOCTYPE html>
        <html>
        <head>
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { height: 100vh; width: 100vw; background: #1b5e20; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([40.41, -3.70], 6);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);
                
                // Marcadores de prueba (como en tu imagen image_2e692e.png)
                L.marker([43.19, -4.83]).addTo(map); // Picos de Europa
                L.marker([36.99, -6.42]).addTo(map); // Doñana
                L.marker([37.06, -3.36]).addTo(map).on('click', function() {
                    Android.onMarkerClick("Sierra Nevada"); 
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                // Creamos la interfaz
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onMarkerClick(parque: String) {
                        // Aquí usamos el navController para ir a detalles
                        navController.navigate(Screens.Parque.route)
                    }
                }, "Android")
                loadDataWithBaseURL(
                    "https://appassets.androidview", // baseUrl
                    htmlMapa,                        // data (tu variable String)
                    "text/html",                    // mimeType
                    "UTF-8",                        // encoding
                    null                            // historyUrl
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
//    AndroidView(
//        factory = { context ->
//            WebView(context).apply {
//                settings.javaScriptEnabled = true // Crucial para Leaflet
//                webViewClient = WebViewClient()
//                loadUrl("file:///android_asset/mapa.html")
//            }
//        },
//        modifier = Modifier.fillMaxSize()
//    )

}