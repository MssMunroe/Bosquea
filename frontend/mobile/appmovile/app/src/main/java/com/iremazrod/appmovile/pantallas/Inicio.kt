package com.iremazrod.appmovile.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.iremazrod.appmovile.R


data class Parque(
    val nombre: String,
    val descripcion: String,
    val imagenRes: Int,
    val imagenIzquierda: Boolean
)
@Composable
fun Inicio() {
    val parques = listOf(
        Parque("Picos de Europa", "Es el primer parque nacional de España, en la cordillera Cantábrica...", R.drawable.logo, true),
        Parque("Ordesa", "Parque nacional desde el 16 de agosto de 1918 y patrimonio mundial...", R.drawable.logo, false)
        // Añade el resto de los 15 parques aquí
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8))
    ) {
        // 1. Encabezado con imagen de fondo y título (image_2e8357.png)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // Tu imagen de fondo
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.7f // Oscurece un poco para que se lea el texto
                )
                Text(
                    text = "La belleza de los 15\nParques Nacionales de España",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 2. Texto introductorio
        item {
            Text(
                text = "Se celebra el centenario de la designación...",
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                color = Color.DarkGray
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Lista de Parques (Alternando imagen y texto)
        items(parques) { parque ->
            ParqueRow(parque)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ParqueRow(parque: Parque) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (parque.imagenIzquierda) {
            ParqueImagen(parque.imagenRes)
            Spacer(modifier = Modifier.width(16.dp))
            ParqueTexto(parque.descripcion)
        } else {
            ParqueTexto(parque.descripcion)
            Spacer(modifier = Modifier.width(16.dp))
            ParqueImagen(parque.imagenRes)
        }
    }
}

@Composable
fun RowScope.ParqueImagen(resId: Int) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = Modifier
            .weight(1f)
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun RowScope.ParqueTexto(texto: String) {
    Text(
        text = texto,
        modifier = Modifier.weight(1.2f),
        fontSize = 14.sp,
        color = Color.Black
    )
}