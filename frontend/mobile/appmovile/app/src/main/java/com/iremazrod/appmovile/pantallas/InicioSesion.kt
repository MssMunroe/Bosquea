package com.iremazrod.appmovile.pantallas

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.ui.theme.Screens

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val verdeOscuro = Color(0xFF4B6332)
    val fondoCrema = Color(0xFFF1F4E8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoCrema)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón Atrás
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = verdeOscuro)
            }
        }

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp).padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo Nickname/Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Nickname o Email") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = verdeOscuro) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
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
            placeholder = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = verdeOscuro) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verdeOscuro,
                unfocusedBorderColor = verdeOscuro
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Iniciar Sesión
        Button(
            onClick = { navController.navigate(Screens.Inicio.route) },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = verdeOscuro),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Iniciar Sesión", fontSize = 18.sp, color = Color.White)
        }

        Text(
            text = "¿Olvidaste la contraseña?",
            modifier = Modifier.padding(16.dp),
            color = Color.Gray,
            fontSize = 12.sp
        )

        // Registro
        Row {
            Text("¿No tienes cuenta? ", color = Color.Gray)
            Text(
                "Registrarse",
                color = verdeOscuro,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate(Screens.Registro.route) {
                    popUpTo(Screens.Inicio.route) { inclusive = true }
                } }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Separador "o"
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
            Text("  o  ", color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Inicia sesión con una red social", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Redes Sociales (Simuladas con iconos circulares)
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
    IconButton(onClick = { /* Accion social */ }) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = Color.Unspecified // Para mantener colores originales si son logos
        )
    }
}