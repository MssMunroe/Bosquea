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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.UserProfileResponse

@Composable
fun Favoritos(navController: NavController) {
    val context = LocalContext.current

    // Estados para los datos del usuario
    var userData by remember { mutableStateOf<UserProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Sincronización: Usamos la misma SharedPreferences que en Login y Perfil
    val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    LaunchedEffect(Unit) {
        if (userId != -1) {
            try {
                // Obtenemos los datos del usuario para el Nickname e Icono
                val response = RetrofitClient.instance.getUserProfile(userId)
                userData = response
            } catch (e: Exception) {
                Log.e("FAVORITOS", "Error: ${e.message}")
                Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
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
            // Reutilizamos el Header que definimos en Perfil.kt para mantener la estética
            // Asegúrate de que PerfilHeader no sea private en Perfil.kt
            PerfilHeader(
                fotoPerfil = userData?.icono ?: "default_user.png",
                navController = navController,
                context = context
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Saludo personalizado
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

            // --- SECCIÓN DE TARJETAS DE CATEGORÍA ---

            // Rutas Favoritas
            FavoritoCard(
                titulo = "Rutas de Senderismo",
                descripcion = "Explora los senderos que has guardado para tu próxima aventura.",
                imagenRes = R.drawable.banner, // Puedes poner una foto de bosque aquí
                onClick = {
                    // Navegación a la lista de rutas filtrada (a implementar)
                    navController.navigate("rutas")
                }
            )

            // Parques Favoritos
            FavoritoCard(
                titulo = "Parques Naturales",
                descripcion = "Tus rincones favoritos de la Red de Parques Nacionales.",
                imagenRes = R.drawable.logo, // O una foto de montaña
                onClick = {
                    // Navegación a la pantalla de inicio o búsqueda filtrada
                    navController.navigate("inicio")
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FavoritoCard(
    titulo: String,
    descripcion: String,
    imagenRes: Int,
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
            // Imagen de la categoría
            Image(
                painter = painterResource(id = imagenRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
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
                    Text("VER GUARDADOS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}