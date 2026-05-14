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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.ui.theme.Screens

@Composable
fun RegistroScreen(navController: NavController) {
    // Estados para los campos
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val verdeOscuro = Color(0xFF4B6332)
    val fondoCrema = Color(0xFFF1F4E8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoCrema)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()), // Por si el teclado tapa campos
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón Atrás
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = verdeOscuro)
            }
        }

        // Logo central
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )

        // Lista de campos (reutilizando el estilo de image_38785f.png)
        CustomRegistrationField(value = nickname, onValueChange = { nickname = it }, label = "Nickname", icon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email)
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(value = dni, onValueChange = { dni = it }, label = "DNI", icon = Icons.Default.Edit)
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(value = password, onValueChange = { password = it }, label = "Contraseña", icon = Icons.Default.Lock, isPassword = true)
        Spacer(modifier = Modifier.height(12.dp))

        CustomRegistrationField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirma la contraseña", icon = Icons.Default.Lock, isPassword = true)

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Registrarse
        Button(
            onClick = { navController.navigate(Screens.Inicio.route) {
                popUpTo(Screens.Splash.route) { inclusive = true }
            } },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = verdeOscuro),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Registrarse", fontSize = 18.sp, color = Color.White)
        }

        // Enlace a Inicio de Sesión
        Row(modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)) {
            Text("¿Ya tienes cuenta? ", color = Color.Gray)
            Text(
                "Inicia Sesión",
                color = verdeOscuro,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate(Screens.Inicio.route) }
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
        placeholder = { Text(label, color = Color.Gray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF4B6332)) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(30.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4B6332),
            unfocusedBorderColor = Color(0xFF4B6332),
            cursorColor = Color(0xFF4B6332)
        )
    )
}