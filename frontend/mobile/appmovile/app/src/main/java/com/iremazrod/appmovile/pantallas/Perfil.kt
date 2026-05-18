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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.UserProfileResponse
import com.iremazrod.appmovile.data.network.UpdateProfileRequest
import com.iremazrod.appmovile.ui.theme.Screens
import androidx.core.content.edit
import kotlinx.coroutines.launch

@Composable
fun Perfil(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. ESTADOS DE LA PANTALLA
    var userData by remember { mutableStateOf<UserProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditWindow by remember { mutableStateOf(false) } // Controla si estamos en modo ver o editar

    // Obtenemos el userId guardado en SharedPreferences durante el Login
    val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    // Función auxiliar para traer o refrescar los datos del perfil
    fun cargarDatosPerfil() {
        if (userId == -1) {
            Toast.makeText(context, "Sesión no válida", Toast.LENGTH_SHORT).show()
            navController.navigate(Screens.Login.route) {
                popUpTo(0)
            }
            return
        }
        scope.launch {
            try {
                isLoading = true
                val response = RetrofitClient.instance.getUserProfile(userId)
                userData = response
            } catch (e: Exception) {
                Log.e("PERFIL", "Error: ${e.message}")
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    // CARGA DE DATOS
    LaunchedEffect(Unit) {
        cargarDatosPerfil()
    }

    // VENTANA COMPLETA DE EDICIÓN
    if (showEditWindow && userData != null) {
        EditarPerfilVentana(
            user = userData!!,
            userId = userId,
            onDismiss = { showEditWindow = false },
            onSaveSuccess = {
                showEditWindow = false
                Toast.makeText(context, "¡Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show()
                cargarDatosPerfil()
            }
        )
    } else {
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
                        // Cabecera con fotos y botones
                        PerfilHeader(
                            fotoPerfil = user.icono ?: "default.png",
                            navController = navController,
                            context = context,
                            onEditClick = { showEditWindow = true }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nickname
                        Text(
                            text = "@${user.nickname}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4B6332)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // INFORMACIÓN PERSONAL
                        Surface(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                InfoPerfilItem(Icons.Default.Person, "Nombre completo", user.nombre ?: "No asignado")
                                InfoPerfilItem(Icons.Default.AlternateEmail, "Nombre de usuario (Nickname)", user.nickname ?: "No asignado")
                                InfoPerfilItem(Icons.Default.Email, "Correo electrónico", user.email ?: "No asignado")
                                InfoPerfilItem(Icons.Default.Phone, "Teléfono de contacto", user.telefono ?: "No asignado")
                                InfoPerfilItem(Icons.Default.Badge, "DNI", user.dni ?: "No asignado")
                                InfoPerfilItem(Icons.Default.Map, "Código Postal", user.codigo_postal ?: "No asignado")
                            }
                        }

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
}

@Composable
fun PerfilHeader(
    fotoPerfil: String,
    navController: NavController,
    context: Context,
    onEditClick: (() -> Unit)? = null
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val imageUrl = fotoPerfil

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
                            onEditClick?.invoke() // Disparamos la apertura del formulario
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
                model = imageUrl,
                contentDescription = "Foto de usuario",
                placeholder = painterResource(R.drawable.logo),
                error = painterResource(R.drawable.logo),
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// NUEVA COMPOSABLE: Ventana completa para la edición cómoda de los 5 campos requeridos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilVentana(
    user: UserProfileResponse,
    userId: Int,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf(user.nombre ?: "") }
    var nickname by remember { mutableStateOf(user.nickname ?: "") }
    var telefono by remember { mutableStateOf(user.telefono ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var contra by remember { mutableStateOf("") } // Vacio por seguridad de cara al usuario
    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Modificar Perfil", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { if (!isSaving) onDismiss() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4B6332))
            )
        },
        containerColor = Color(0xFFF1F4E8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Modifica los campos que estimes necesarios para actualizar tu cuenta.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF4B6332)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4B6332)),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nombre de Usuario (Nickname)") },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, null, tint = Color(0xFF4B6332)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4B6332)),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF4B6332)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4B6332)),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de Contacto") },
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color(0xFF4B6332)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4B6332)),
                enabled = !isSaving
            )

            OutlinedTextField(
                value = contra,
                onValueChange = { contra = it },
                label = { Text("Nueva Contraseña (Opcional)") },
                placeholder = { Text("Dejar en blanco para no cambiar") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF4B6332)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF4B6332)),
                enabled = !isSaving
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || nickname.isBlank() || email.isBlank()) {
                        Toast.makeText(context, "Nombre, Nickname y Email son obligatorios.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        isSaving = true
                        try {
                            val request = UpdateProfileRequest(
                                nombre = nombre.trim(),
                                nickname = nickname.trim(),
                                email = email.trim(),
                                telefono = if (telefono.isNotBlank()) telefono.trim() else null,
                                contra = if (contra.isNotBlank()) contra.trim() else null
                            )

                            val response = RetrofitClient.instance.updateUserProfile(userId, request)

                            if (response.isSuccessful && response.body()?.estado == true) {
                                onSaveSuccess()
                            } else {
                                val errorMensaje = response.body()?.mensaje ?: "Error al actualizar"
                                Toast.makeText(context, errorMensaje, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Log.e("UPDATE_PROFILE", "Error: ${e.message}")
                            Toast.makeText(context, "Error de conexión de red", Toast.LENGTH_SHORT).show()
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6332)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// Mantener la función original para que no haya errores de dependencias de llamadas externas
@Composable
fun EditarPerfilDialog(
    user: UserProfileResponse,
    userId: Int,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    // Esta función queda depreciada internamente a favor de la vista completa, pero no se borra
    // para asegurar la compatibilidad total de tu proyecto.
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