package com.iremazrod.appmovile.pantallas

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.SearchResponse
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.*
@Composable
fun Buscar(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var resultados by remember { mutableStateOf<List<SearchResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animacion_busqueda))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    // 1. LÓGICA DE BÚSQUEDA CON DEBOUNCE
    // Esperamos 500ms después de que el usuario deje de escribir para no saturar la API
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            delay(500)
            isLoading = true
            try {
                val response = RetrofitClient.instance.buscar(searchQuery)
                resultados = response
            } catch (e: Exception) {
                Log.e("BUSQUEDA", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        } else {
            resultados = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8)) // Fondo crema
    ) {
        // Barra de búsqueda personalizada
        BosqueaSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4B6332))
            }
        } else if (searchQuery.isEmpty()) {
            // ESTADO INICIAL: Animación de "Esperando que busques"
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(250.dp)
                )
                Text("¿Qué parque quieres visitar hoy?", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val parques = resultados.filter { it.tipo == "parque" }
                val rutas = resultados.filter { it.tipo == "ruta" }

                if (parques.isNotEmpty()) {
                    item { SectionHeader("PARQUES NATURALES") }
                    items(parques) { item ->
                        RecommendationCard(
                            nombre = item.nombre,
                            subtexto = "Espacio Protegido",
                            imagenUrl = if (item.url.isNotEmpty()) "http://10.0.2.2:5000/static/img/${item.url}" else null,
                            onClick = { navController.navigate("parque/${item.nombre}") }
                        )
                    }
                }

                if (searchQuery.length >= 3 && resultados.isEmpty()) {
                    item { EmptySearchState(searchQuery) }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(nombre: String, subtexto: String, imagenUrl: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imagenUrl != null) {
                AsyncImage(
                    model = imagenUrl,
                    contentDescription = null,
                    modifier = Modifier.size(65.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.logo)
                )
            } else {
                // Icono por defecto (Verde corporativo)
                Surface(
                    color = Color(0xFFF1F4E8),
                    modifier = Modifier.size(65.dp).clip(RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = Color(0xFF4B6332)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = subtexto,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B6332),
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun BosqueaSearchBar(query: String, onQueryChange: (String) -> Unit) {
    var showFilter by remember { mutableStateOf(false) }

    Column(modifier = Modifier.background(Color.White).padding(bottom = 8.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Buscar parques o senderos...") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF4B6332)) },
            trailingIcon = {
                IconButton(onClick = { showFilter = !showFilter }) {
                    Icon(Icons.AutoMirrored.Filled.List, null, tint = Color.Gray)
                }
            },
            shape = RoundedCornerShape(35.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4B6332),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )

        AnimatedVisibility(visible = showFilter) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color(0xFFF1F4E8), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip("Rutas", isSelected = true)
                FilterChip("Parques", isSelected = false)
                FilterChip("Cerca de mí", isSelected = false)
            }
        }
    }
}

@Composable
fun EmptySearchState(query: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Search, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No encontramos resultados para\n\"$query\"",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFF4B6332) else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF4B6332) else Color(0xFFE0E0E0)),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}