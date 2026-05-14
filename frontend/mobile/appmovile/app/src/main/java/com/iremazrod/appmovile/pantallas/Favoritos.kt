package com.iremazrod.appmovile.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iremazrod.appmovile.R

@Composable
fun Favoritos(navController: NavController) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8)) // Fondo crema
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Reutilizamos la cabecera de la pantalla de Perfil
        PerfilHeader(
            menuExpanded = menuExpanded,
            onDismiss = { menuExpanded = false },
            onDeleteClick = {
                showDeleteDialog = true // ¡Aquí sí puedes reasignar!
            },
            navController
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "@MssNatura",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Card de Rutas
        FavoritoCard(
            titulo = "Rutas",
            cantidad = "4 rutas guardadas",
            imagenRes = R.drawable.logo, // La ilustración de los pines
            onClick = { /* Navegar a lista detallada de rutas fav */ }
        )

        // Card de Parques Naturales
        FavoritoCard(
            titulo = "Parques Naturales",
            cantidad = "8 parques guardados",
            imagenRes = R.drawable.logo, // La ilustración de la montaña
            onClick = { /* Navegar a lista detallada de parques fav */ }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun FavoritoCard(
    titulo: String,
    cantidad: String,
    imagenRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Imagen superior de la card
            Image(
                painter = painterResource(id = imagenRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Fit // Fit para que se vean bien las ilustraciones
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = titulo,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = cantidad,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Botón "Ver guardadas"
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6332)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Ver guardadas", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}