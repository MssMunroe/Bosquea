package com.iremazrod.appmovile.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iremazrod.appmovile.R

@Composable
fun Buscar() {
    var searchQuery by remember { mutableStateOf("") }
    val fondoCrema = Color(0xFFF1F4E8)

    Column(modifier = Modifier.fillMaxSize().background(fondoCrema)) {
        BosqueaSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Sección Parques
            item {
                SectionHeader("Parques más visitados este mes")
            }
            items(3) { index -> // Aquí mapearás tus datos reales luego
                RecommendationCard("Teide", "37.000 visitantes", R.drawable.logo)
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // Sección Rutas
            item {
                SectionHeader("Rutas más visitadas este mes")
            }
            items(3) { index ->
                RecommendationCard("Ruta del Cares", "", R.drawable.logo)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        HorizontalDivider(color = Color.Black, thickness = 0.5.dp)
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun BosqueaSearchBar(query: String, onQueryChange: (String) -> Unit) {
    var showFilter by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Buscar ruta o parque natural...") },
            trailingIcon = {
                IconButton(onClick = { showFilter = !showFilter }) {
                    Icon(Icons.AutoMirrored.Filled.List, null)
                }
            },
            shape = RoundedCornerShape(35.dp)
        )

        // El filtro de image_a2949c.png
        AnimatedVisibility(visible = showFilter) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color(0xFFC5E1A5), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip("Rutas", isSelected = true)
                FilterChip("Parques", isSelected = false)
                FilterChip("Guardados", isSelected = false)
            }
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFF4B3D21) else Color(0xFFFFE0B2),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (isSelected) Color.White else Color(0xFF4B3D21),
            fontSize = 14.sp
        )
    }
}

@Composable
fun RecommendationCard(nombre: String, subtexto: String, imagenRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD2B48C)) // Un tono arena/marrón claro como el de tu imagen
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imagenRes),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nombre, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4B3D21))
            }
            Text(text = subtexto, fontSize = 12.sp, color = Color.Gray)
        }
    }
}