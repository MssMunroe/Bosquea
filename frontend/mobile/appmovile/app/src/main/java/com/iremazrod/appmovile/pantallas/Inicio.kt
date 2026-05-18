package com.iremazrod.appmovile.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.ParqueResponse
import com.iremazrod.appmovile.data.network.RetrofitClient

@Composable
fun Inicio(navController: NavController) {
    // Estado para la lista de parques
    var listaParques by remember { mutableStateOf<List<ParqueResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Efecto de carga inicial
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.instance.getParques()
            listaParques = response
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error al cargar parques: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8))
    ) {
        // --- CABECERA ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.banner),
                    contentDescription = "Banner Parques Nacionales",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))

                Text(
                    text = "Explora la belleza de los\nParques Nacionales",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- ESTADO DE CARGA O LISTA ---
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxHeight(0.6f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4B6332))
                }
            }
        } else {
            itemsIndexed(listaParques) { index, parque ->
                ParqueRow(
                    parque = parque,
                    imagenIzquierda = index % 2 == 0,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ParqueRow(parque: ParqueResponse, imagenIzquierda: Boolean, navController: NavController) {
    val imageUrl = parque.img

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { navController.navigate("parque/${parque.nombre}") },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imagenIzquierda) {
            ParqueImagen(imageUrl)
            Spacer(modifier = Modifier.width(16.dp))
            ParqueTexto(parque.nombre, "Situado en ${parque.ubicacion}. Con una extensión de ${parque.tamanio}.")
        } else {
            ParqueTexto(parque.nombre, "Situado en ${parque.ubicacion}. Con una extensión de ${parque.tamanio}.")
            Spacer(modifier = Modifier.width(16.dp))
            ParqueImagen(imageUrl)
        }
    }
}

@Composable
fun RowScope.ParqueImagen(url: String?) {
    AsyncImage(
        model = url,
        contentDescription = "Foto del Parque",
        error = painterResource(R.drawable.logo),
        placeholder = painterResource(R.drawable.logo),
        modifier = Modifier
            .weight(1f)
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun RowScope.ParqueTexto(nombre: String, info: String) {
    Column(modifier = Modifier.weight(1.2f)) {
        Text(
            text = nombre.uppercase(),
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF4B6332)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = info,
            fontSize = 13.sp,
            color = Color(0xFF2C2C2C),
            maxLines = 5,
            textAlign = TextAlign.Justify,
            lineHeight = 18.sp
        )
        Text(
            text = "Leer más...",
            fontSize = 12.sp,
            color = Color(0xFF4B6332),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}