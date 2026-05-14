package com.iremazrod.appmovile.pantallas

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Reporte(navController: NavController) {
    // Estados para el formulario
    var nombre by remember { mutableStateOf("Scarlett") }
    var email by remember { mutableStateOf("scarlett22@gmail.com") }
    var telefono by remember { mutableStateOf("+34 945 425 187") }
    var mensaje by remember { mutableStateOf("") }

    // Estados para el Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF1F4E8) // Fondo crema general
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Reutilizamos la cabecera de perfil (image_a362d0.png)
            PerfilHeader(
                menuExpanded = menuExpanded,
                onDismiss = { menuExpanded = false },
                onDeleteClick = {
                },
                navController
            )

            Text(
                text = "@MssNatura",
                fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Contenedor del Formulario (Verde clarito)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC5E1A5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FORMULARIO DE CONTACTO",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp),
                        color = Color(0xFF2E3D1E)
                    )

                    ReportField("NOMBRE", nombre) { nombre = it }
                    ReportField("EMAIL", email) { email = it }
                    ReportField("TELÉFONO", telefono) { telefono = it }
                    ReportField("MENSAJE", mensaje, isLarge = true) { mensaje = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón ENVIAR
                    Button(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Formulario enviado")
                                // Pequeño delay para que dé tiempo a ver el snackbar antes de volver
                                delay(500)
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6332)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ENVIAR", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportField(label: String, value: String, isLarge: Boolean = false, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF4B6332), fontWeight = FontWeight.Bold)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLarge) 150.dp else 50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF9FBE7),
                unfocusedContainerColor = Color(0xFFF9FBE7),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}