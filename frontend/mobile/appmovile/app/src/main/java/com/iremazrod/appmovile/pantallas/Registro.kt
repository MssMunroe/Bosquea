package com.iremazrod.appmovile.pantallas

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.ui.theme.Screens
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(navController: NavController) {
    // 1. ESTADOS DE LOS CAMPOS (Sincronizados con app.py)
    var nombre by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contra by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var cp by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val verdeOscuro = Color(0xFF4B6332)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8))
            .verticalScroll(rememberScrollState()) // Permite scroll si el teclado tapa campos
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón Atrás
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = verdeOscuro)
            }
        }

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )

        Text(
            text = "CREAR CUENTA",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = verdeOscuro,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 2. CAMPOS DE FORMULARIO
        CustomRegistrationField(nombre, { nombre = it }, "Nombre Completo", Icons.Default.Person)
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(nickname, { nickname = it }, "Nickname", Icons.Default.Face)
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(email, { email = it }, "Email", Icons.Default.Email)
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(contra, { contra = it }, "Contraseña", Icons.Default.Lock, isPassword = true)
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(dni, { dni = it }, "DNI", Icons.Default.Badge) // Badge es más apropiado para DNI
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(cp, { cp = it }, "Código Postal", Icons.Default.LocationOn)

        Spacer(modifier = Modifier.height(32.dp))

        // 3. BOTÓN DE REGISTRO
        Button(
            onClick = {
                // Validación básica antes de llamar a la API
                if (nombre.isBlank() || nickname.isBlank() || email.isBlank() || contra.isBlank() || dni.isBlank() || cp.isBlank()) {
                    Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                scope.launch {
                    try {
                        // Llamada a la API usando los parámetros Field que definimos en BosqueaApiService
                        val response = RetrofitClient.instance.register(
                            nombre = nombre,
                            nickname = nickname,
                            email = email,
                            contra = contra,
                            dni = dni,
                            cp = cp
                        )

                        if (response.isSuccessful) {
                            Toast.makeText(context, "¡Usuario creado! Ya puedes iniciar sesión", Toast.LENGTH_LONG).show()
                            // Al registrarse con éxito, vamos al Login
                            navController.navigate(Screens.Login.route) {
                                popUpTo(Screens.Registro.route) { inclusive = true }
                            }
                        } else {
                            val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                            Log.e("REGISTRO", "Error servidor: $errorMsg")
                            Toast.makeText(context, "Error: El usuario ya existe o datos inválidos", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e("REGISTRO", "Fallo red", e)
                        Toast.makeText(context, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = verdeOscuro),
            shape = RoundedCornerShape(30.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("REGISTRARSE", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Enlace para volver al Login
        Row(modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)) {
            Text("¿Ya eres miembro? ", color = Color.Gray)
            Text(
                "Inicia Sesión",
                color = verdeOscuro,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Registro.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun CustomRegistrationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) }, // Usamos label en vez de placeholder para mejor UX
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF4B6332)) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(30.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4B6332),
            unfocusedBorderColor = Color(0xFF4B6332),
            focusedLabelColor = Color(0xFF4B6332)
        )
    )
}