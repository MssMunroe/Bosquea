package com.iremazrod.appmovile.pantallas

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.ParqueResponse
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.FavoritoToggleRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Parque(nombreParque: String, navController: NavController) {
    var parque: ParqueResponse? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var esFavorito by remember { mutableStateOf(false) }
    var esVisitado by remember { mutableStateOf(false) }

    val userId = context.getSharedPreferences("auth", Context.MODE_PRIVATE).getInt("userId", -1)

    LaunchedEffect(nombreParque) {
        try {
            val p = RetrofitClient.instance.getParqueDetalle(nombreParque)
            parque = p
        } catch (e: Exception) {
            Log.e("PARQUE_DETALLE", "Error: ${e.message}")
            Toast.makeText(context, "No se pudo cargar la información del parque", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color(0xFF4B6332))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF1F4E8)
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4B6332))
            }
        } else if (parque != null) {
            val p = parque!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF1F4E8))
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = p.nombre.uppercase(),
                    fontSize = 26.sp,
                    color = Color(0xFF4B3D21),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp).width(150.dp),
                    thickness = 2.dp,
                    color = Color(0xFF4B6332)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionIconButton(
                        icon = if (esVisitado) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        label = if (esVisitado) "Visitado" else "Marcar visita",
                        color = if (esVisitado) Color(0xFF4B6332) else Color.Gray,
                        onClick = { esVisitado = !esVisitado }
                    )

                    Spacer(modifier = Modifier.width(40.dp))

                    ActionIconButton(
                        icon = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        label = "Favorito",
                        color = if (esFavorito) Color.Red else Color.Gray,
                        onClick = {
                            if (userId == -1) {
                                Toast.makeText(context, "Inicia sesión para guardar favoritos", Toast.LENGTH_SHORT).show()
                                return@ActionIconButton
                            }
                            scope.launch {
                                try {
                                    val request = FavoritoToggleRequest(id_usuario = userId, id_parque = p.id)
                                    val response = RetrofitClient.instance.toggleFavorito(request)
                                    if (response.isSuccessful) {
                                        esFavorito = response.body()?.estado ?: !esFavorito
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "{$e} Error al actualizar favoritos", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }

                // --- CAMBIO AQUÍ: Usamos directamente p.img ---
                AsyncImage(
                    model = p.img,
                    contentDescription = p.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.logo),
                    error = painterResource(R.drawable.logo)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = p.descripcion,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify,
                        color = Color(0xFF2C2C2C),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EDDA)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("FICHA TÉCNICA", fontWeight = FontWeight.ExtraBold, color = Color(0xFF4B6332), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        TechRow("Superficie", p.tamanio)
                        TechRow("Ubicación", p.ubicacion)
                        TechRow("Coordenadas", "${p.lat}, ${p.lon}")
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ActionIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(30.dp))
        }
        Text(text = label, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TechRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$label: ", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF4B3D21))
        Text(text = value, fontSize = 14.sp, color = Color.Black)
    }
}