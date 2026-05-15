package com.iremazrod.appmovile.pantallas

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.UserProfileResponse
import com.iremazrod.appmovile.ui.theme.Screens
import androidx.core.content.edit

@Composable
fun Perfil(navController: NavController) {
    val context = LocalContext.current

    // 1. ESTADOS DE LA PANTALLA
    var userData by remember { mutableStateOf<UserProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Obtenemos el userId guardado en SharedPreferences durante el Login
    val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    // 2. CARGA DE DATOS DESDE LA API
    LaunchedEffect(Unit) {
        if (userId == -1) {
            Toast.makeText(context, "Sesión no válida", Toast.LENGTH_SHORT).show()
            navController.navigate(Screens.Login.route) {
                popUpTo(0) // Limpiar historial para obligar login
            }
            return@LaunchedEffect
        }

        try {
            // Llamada al endpoint /api/user/<id>
            val response = RetrofitClient.instance.getUserProfile(userId)
            userData = response
        } catch (e: Exception) {
            Log.e("PERFIL", "Error: ${e.message}")
            Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
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
            userData?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Cabecera con fotos y botones de navegación
                    PerfilHeader(
                        fotoPerfil = user.icono ?: "default_user.png",
                        navController = navController,
                        context = context
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nickname destacado
                    Text(
                        text = "@${user.nickname}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4B6332)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. TARJETA DE INFORMACIÓN PERSONAL
                    Surface(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        tonalElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            InfoPerfilItem(Icons.Default.Person, "Nombre completo", user.nombre)
                            InfoPerfilItem(Icons.Default.Email, "Correo electrónico", user.email)
                            InfoPerfilItem(Icons.Default.Badge, "DNI", user.dni)
                            InfoPerfilItem(Icons.Default.Map, "Código Postal", user.codigo_postal)
                        }
                    }

                    // Badge de Admin si corresponde
                    if ((user.rol_id ?: 0) == 1) {
                        Card(
                            modifier = Modifier.padding(top = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4B6332)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "ADMINISTRADOR",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun PerfilHeader(
    fotoPerfil: String,
    navController: NavController,
    context: Context
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) } // Estado para el diálogo de edición

    // --- CAMBIO DE IMAGEN: Usamos la URL que viene de la BBDD directamente ---
    // Si tu BBDD devuelve "https://raw.githubusercontent.com...", esto funcionará solo.
    val imageUrl = fotoPerfil

    // Diálogo de "En desarrollo" para Editar Perfil
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Entendido") }
            },
            title = { Text("Editar Perfil") },
            text = { Text("Esta funcionalidad estará disponible en la próxima actualización de Bosquea.") }
        )
    }

    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        // Banner
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
            contentScale = ContentScale.Crop
        )

        // Botones superiores
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White)
            }

            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.MoreVert, "Opciones", tint = Color.White)
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar Perfil") },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = {
                            menuExpanded = false
                            showEditDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión", color = Color.Red) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.Red) },
                        onClick = {
                            context.getSharedPreferences("auth", Context.MODE_PRIVATE).edit { clear() }
                            navController.navigate(Screens.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // FOTO DE PERFIL
        Surface(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.BottomCenter)
                .border(4.dp, Color(0xFFF1F4E8), CircleShape),
            shape = CircleShape,
            color = Color.White
        ) {
            AsyncImage(
                model = imageUrl, // <--- Carga directa de la URL de GitHub/BBDD
                contentDescription = "Foto de usuario",
                placeholder = painterResource(R.drawable.logo),
                error = painterResource(R.drawable.logo),
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun InfoPerfilItem(icon: ImageVector, label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4B6332),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50)
                )
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        thickness = 0.5.dp,
        color = Color(0xFFF0F0F0)
    )
}