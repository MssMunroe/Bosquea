package com.iremazrod.appmovile.pantallas

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.UserProfileResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Reporte(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados para los datos
    var userData by remember { mutableStateOf<UserProfileResponse?>(null) }
    var mensaje by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSending by remember { mutableStateOf(false) }

    // Sincronización de SharedPreferences
    val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    // Cargar datos del usuario para autocompletar el formulario
    LaunchedEffect(Unit) {
        try {
            if (userId != -1) {
                val response = RetrofitClient.instance.getUserProfile(userId)
                userData = response
            }
        } catch (e: Exception) {
            Log.e("REPORTE", "Error: ${e.message}")
        } finally {
            isLoading = false // Corregido: Ahora siempre deja de cargar
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF1F4E8)
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4B6332))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Reutilizamos la cabecera estética de Perfil
                PerfilHeader(
                    fotoPerfil = userData?.icono ?: "default_user.png",
                    navController = navController,
                    context = context
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CENTRO DE AYUDA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4B6332)
                )

                Text(
                    text = "Cuéntanos qué sucede, @${userData?.nickname ?: "usuario"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // --- TARJETA DEL FORMULARIO ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        // Campos informativos (No editables para mantener integridad del reporte)
                        StaticInfoField(label = "NOMBRE DEL REMITENTE", value = userData?.nombre ?: "No identificado")
                        StaticInfoField(label = "CORREO ELECTRÓNICO", value = userData?.email ?: "No disponible")

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Campo de mensaje (Editable)
                        Text(
                            text = "DETALLES DEL REPORTE",
                            fontSize = 12.sp,
                            color = Color(0xFF4B6332),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = mensaje,
                            onValueChange = { mensaje = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            placeholder = { Text("Describe el problema o sugerencia aquí...", fontSize = 14.sp) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4B6332),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de Envío
                        Button(
                            onClick = {
                                if (mensaje.isNotBlank()) {
                                    scope.launch {
                                        isSending = true
                                        // Simulación de envío a API o envío real
                                        delay(1500)
                                        isSending = false
                                        snackbarHostState.showSnackbar("Reporte enviado correctamente. Revisaremos tu caso.")
                                        delay(1000)
                                        navController.popBackStack()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6332)),
                            enabled = mensaje.isNotBlank() && !isSending,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isSending) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("ENVIAR REPORTE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun StaticInfoField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color(0xFF2C3E50),
            fontWeight = FontWeight.Medium
        )
    }
}