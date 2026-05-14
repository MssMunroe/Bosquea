package com.iremazrod.appmovile.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.iremazrod.appmovile.R

data class Ruta(
    val nombre: String,
    val telefono: String,
    val email: String,
    val imagenRes: Int,
    val parque: String, // Para agruparlas
    var esFavorito: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class) // Necesario para stickyHeader
@Composable
fun Rutas() {
    // Datos de ejemplo basados en tu imagen
    val todasLasRutas = listOf(
        Ruta("Laguna del Jaral", "+34 959 439 629", "infojaral.visitante@juntadeandalucia.es", R.drawable.logo, "Parque Doñana"),
        Ruta("Ruta de la Rocina", "+34 959 439 569", "cvrocina.pndonana.cagpds@juntadeandalucia.es", R.drawable.logo, "Parque Doñana", true),
        Ruta("Ruta del Cares", "+34 942 738 109", "administracioncangas1@pnpeu.es", R.drawable.logo, "Parque Picos de Europa"),
        Ruta("Vega de Ario", "+34 942 738 109", "administracioncangas2@pnpeu.es", R.drawable.logo, "Parque Picos de Europa")
    )

    // Agrupamos las rutas por el nombre del parque
    val rutasAgrupadas = todasLasRutas.groupBy { it.parque }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8)) // Tu fondo crema
    ) {
        rutasAgrupadas.forEach { (parque, rutas) ->
            // Cabecera del Parque (Se queda fija al hacer scroll)
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F4E8))
                ) {
                    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                    Text(
                        text = parque,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                }
            }

            // Cards de las rutas de ese parque
            items(rutas) { ruta ->
                RutaCard(ruta)
            }
        }
    }
}

@Composable
fun RutaCard(ruta: Ruta) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFBCA371)) // Color ocre de la imagen
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la ruta
            Image(
                painter = painterResource(id = ruta.imagenRes),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ruta.nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B3D21)
                    )
                    // Icono de favorito (Corazón)
                    Icon(
                        imageVector = if (ruta.esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = Color(0xFF5D6B32), // Verde oscuro
                        modifier = Modifier.size(24.dp).clickable { /* Lógica favoritos */ }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = ruta.telefono, fontSize = 14.sp, color = Color.Black)
                Text(text = ruta.email, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}