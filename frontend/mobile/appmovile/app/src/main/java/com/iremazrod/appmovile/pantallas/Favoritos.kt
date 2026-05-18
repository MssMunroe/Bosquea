package com.iremazrod.appmovile.pantallas

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.UserProfileResponse

enum class VistaFavoritos {
    CATEGORIAS,
    LISTA_DESEOS,
    VISITADOS
}

@Composable
fun Favoritos(navController: NavController) {
    val context = LocalContext.current

    // Estados para los datos del usuario
    var userData by remember { mutableStateOf<UserProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Estado para controlar qué listado ver
    var vistaActual by remember { mutableStateOf(VistaFavoritos.CATEGORIAS) }

    // Sincronización con SharedPreferences
    val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    LaunchedEffect(Unit) {
        if (userId != -1) {
            try {
                val response = RetrofitClient.instance.getUserProfile(userId)
                userData = response
            } catch (e: Exception) {
                Log.e("FAVORITOS", "Error: ${e.message}")
                Toast.makeText(context, "Error al cargar tus listas guardadas", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxHeight(0.7f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4B6332))
            }
        } else {
            // Cabecera
            PerfilHeader(
                fotoPerfil = userData?.icono ?: "default_user.png",
                navController = navController,
                context = context
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (vistaActual) {
                VistaFavoritos.CATEGORIAS -> {
                    // --- SELECCIÓN DE CATEGORÍAS ---
                    Text(
                        text = "Tus Guardados",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4B6332)
                    )
                    Text(
                        text = "@${userData?.nickname ?: "usuario"}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // --- LISTA DE DESEOS ---
                    FavoritoCard(
                        titulo = "Mi Lista de Deseos",
                        descripcion = "Visualiza los parques naturales que tienes pendientes por explorar y guardados en tu radar (${userData?.estadisticas?.lista_deseos ?: 0} parques).",
                        imagenRes = R.drawable.banner,
                        textoBoton = "VER MI LISTA",
                        onClick = { vistaActual = VistaFavoritos.LISTA_DESEOS }
                    )

                    // --- PARQUES VISITADOS ---
                    FavoritoCard(
                        titulo = "Espacios Visitados",
                        descripcion = "Recuerda las áreas de la Red de Parques Nacionales que ya has conquistado y completado (${userData?.estadisticas?.parques_visitados ?: 0} parques).",
                        imagenRes = R.drawable.logo,
                        textoBoton = "VER VISITADOS",
                        onClick = { vistaActual = VistaFavoritos.VISITADOS }
                    )
                }

                VistaFavoritos.LISTA_DESEOS -> {
                    // --- PARQUES DE LA LISTA DE DESEOS ---
                    SeccionCabeceraListado(
                        titulo = "Mi Lista de Deseos",
                        onVolver = { vistaActual = VistaFavoritos.CATEGORIAS }
                    )

                    val deseados = userData?.lista_deseados ?: emptyList()
                    if (deseados.isEmpty()) {
                        EstadoListaVacia("Aún no has añadido ningún parque a tu lista de deseos.")
                    } else {
                        deseados.forEach { parque ->
                            ParqueMinisCard(
                                nombre = parque.nombre,
                                ubicacion = parque.ubicacion,
                                imageUrl = parque.img,
                                onClick = { navController.navigate("parque/${parque.nombre}") }
                            )
                        }
                    }
                }

                VistaFavoritos.VISITADOS -> {
                    // --- PARQUES VISITADOS ---
                    SeccionCabeceraListado(
                        titulo = "Parques Visitados",
                        onVolver = { vistaActual = VistaFavoritos.CATEGORIAS }
                    )

                    val visitados = userData?.lista_visitados ?: emptyList()
                    if (visitados.isEmpty()) {
                        EstadoListaVacia("No has marcado ningún parque natural como visitado todavía.")
                    } else {
                        visitados.forEach { parque ->
                            ParqueMinisCard(
                                nombre = parque.nombre,
                                ubicacion = parque.ubicacion,
                                imageUrl = parque.img,
                                onClick = { navController.navigate("parque/${parque.nombre}") }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FavoritoCard(
    titulo: String,
    descripcion: String,
    imagenRes: Int,
    textoBoton: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imagenRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descripcion,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6332)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(textoBoton, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun SeccionCabeceraListado(titulo: String, onVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onVolver) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver a categorías",
                tint = Color(0xFF4B6332)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = titulo,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B6332)
        )
    }
}

@Composable
fun ParqueMinisCard(
    nombre: String,
    ubicacion: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen del Parque",
                error = painterResource(R.drawable.logo),
                placeholder = painterResource(R.drawable.logo),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre.uppercase(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4B6332)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Situado en $ubicacion",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun EstadoListaVacia(mensaje: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = mensaje,
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}