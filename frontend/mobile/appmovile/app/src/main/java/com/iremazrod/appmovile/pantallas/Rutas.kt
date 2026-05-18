package com.iremazrod.appmovile.pantallas

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.RutaResponse

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Rutas() {
    // almacenar las rutas por el nombre del parque
    var rutasByParque by remember { mutableStateOf<Map<String, List<RutaResponse>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Carga de datos
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.instance.getAllRoutes()
            rutasByParque = response.groupBy { it.parque_nombre ?: "Varios" }
        } catch (e: Exception) {
            Toast.makeText(context, "{$e} Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F4E8))) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF4B6332)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Iteramos sobre el mapa
                rutasByParque.forEach { (parqueNombre, rutas) ->

                    // Cabecera
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFF1F4E8),
                            tonalElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Place,
                                    contentDescription = null,
                                    tint = Color(0xFF4B6332),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = parqueNombre.uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF4B6332),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    // Lista de rutas
                    items(rutas) { ruta ->
                        RutaSimpleCard(ruta)
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun RutaSimpleCard(ruta: RutaResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ruta.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Etiqueta de Dificultad
                    val (diffColor, diffBg) = when (ruta.dificultad.lowercase()) {
                        "alta" -> Color(0xFFC0392B) to Color(0xFFFFEBEE)
                        "media" -> Color(0xFFD35400) to Color(0xFFFFF3E0)
                        else -> Color(0xFF27AE60) to Color(0xFFE8F5E9)
                    }

                    Surface(
                        color = diffBg,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = ruta.dificultad.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = diffColor
                        )
                    }
                }

                // Icono informativo
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Detalles",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (!ruta.web.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                Text(
                    text = "Más información: ${ruta.web}",
                    fontSize = 12.sp,
                    color = Color(0xFF3498DB),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}