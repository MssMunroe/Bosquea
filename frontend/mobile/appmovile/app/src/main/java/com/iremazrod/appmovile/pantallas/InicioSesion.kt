package com.iremazrod.appmovile.pantallas

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.LoginRequest
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.ui.theme.Screens
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    // Estados para los campos de texto
    var emailOrNick by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val verdeOscuro = Color(0xFF4B6332)
    val fondoCrema = Color(0xFFF1F4E8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoCrema)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón Atrás (Vuelve al Splash o sale de la app)
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = verdeOscuro)
            }
        }

        // Logo de la App
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Bosquea",
            modifier = Modifier.size(120.dp).padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo Identificador (Nickname o Email según tu app.py)
        OutlinedTextField(
            value = emailOrNick,
            onValueChange = { emailOrNick = it },
            label = { Text("Nickname o Email") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = verdeOscuro) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            singleLine = true,
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeOscuro,
                unfocusedBorderColor = verdeOscuro
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = verdeOscuro) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            singleLine = true,
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeOscuro,
                unfocusedBorderColor = verdeOscuro
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Iniciar Sesión con lógica de conexión
        Button(
            onClick = {
                if (emailOrNick.isNotBlank() && password.isNotBlank()) {
                    isLoading = true
                    scope.launch {
                        try {
                            // Llamada a la API (LoginRequest mapeado con app.py)
                            val response = RetrofitClient.instance.login(LoginRequest(emailOrNick, password))

                            if (response.isSuccessful && response.body() != null) {
                                val loginResponse = response.body()!!
                                val user = loginResponse.usuario

                                // PERSISTENCIA: Corregimos el nombre de la llave aquí
                                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                                prefs.edit().apply {
                                    // CAMBIA "user_id" POR "userId"
                                    putInt("userId", user.id)
                                    putString("nickname", user.nickname)
                                    putString("email", user.email)
                                    putInt("rol_id", user.rol_id)
                                    apply()
                                }

                                Toast.makeText(context, "¡Hola de nuevo, ${user.nickname}!", Toast.LENGTH_SHORT).show()

                                // Navegamos a Inicio y eliminamos la pantalla de Login de la pila
                                navController.navigate(Screens.Inicio.route) {
                                    popUpTo(Screens.Login.route) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
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
                Text("ENTRAR", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace a Registro
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿Nuevo por aquí? ", color = Color.Gray)
            Text(
                "Crea una cuenta",
                color = verdeOscuro,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Screens.Registro.route)
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Decoración inferior: Redes Sociales
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text("  o conecta con  ", color = Color.Gray, fontSize = 12.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SocialIcon(R.drawable.facebook)
            Spacer(modifier = Modifier.width(20.dp))
            SocialIcon(R.drawable.instagram)
            Spacer(modifier = Modifier.width(20.dp))
            SocialIcon(R.drawable.google)
        }
    }
}

@Composable
fun SocialIcon(iconRes: Int) {
    IconButton(onClick = { /* Implementar en el futuro */ }) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(35.dp),
            tint = Color.Unspecified // Mantiene los colores originales de los logos
        )
    }
}