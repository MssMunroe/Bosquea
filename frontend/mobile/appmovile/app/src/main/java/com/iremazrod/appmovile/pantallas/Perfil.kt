package com.iremazrod.appmovile.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.ui.theme.Screens
import com.iremazrod.appmovile.ui.theme.UserProfile

@Composable
fun Perfil(navController: NavController) {

    var user by remember {
        mutableStateOf(
            UserProfile(
                username = "@MssNatura",
                nombre = "Scarlett",
                email = "scarlett22@gmail.com",
                telefono = "+34 945 425 187",
                dni = "54426673S",
                codigoPostal = "46200",
                fotoPerfil = R.drawable.logo,
                fotoPortada = R.drawable.logo
            )
        )
    }

    var isEditing by remember { mutableStateOf(false) }
    var tempUser by remember(isEditing) { mutableStateOf(user) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    var coloresOutlined = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF4B6332),
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color(0xFF4B6332)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8)) // Fondo crema
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PerfilHeader(
            menuExpanded = menuExpanded,
            onDismiss = { menuExpanded = false },
            onDeleteClick = {
                showDeleteDialog = true
            },
            navController
        )

        if (showDeleteDialog) {
            DeleteAccountDialog(
                onConfirm = {
                    showDeleteDialog = false
                    // Navega de vuelta al login
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) // Limpiar el historial
                    }
                },
                onDismiss = { showDeleteDialog = false } // Solo cierra el aviso y se queda en el perfil
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre de usuario con botón editar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "@MssNatura", fontSize = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { isEditing = true }) {
                Icon(Icons.Default.Edit, null, tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isEditing) {
            // --- VISTA DE EDICIÓN ---
            Text("Editando Perfil", fontWeight = FontWeight.Bold, color = Color(0xFF4B6332))

            OutlinedTextField(
                value = tempUser.nombre,
                onValueChange = { tempUser = tempUser.copy(nombre = it) },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = coloresOutlined
            )

            OutlinedTextField(
                value = tempUser.telefono,
                onValueChange = { tempUser = tempUser.copy(telefono = it) },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = coloresOutlined

            )

            OutlinedTextField(
                value = tempUser.dni,
                onValueChange = { tempUser = tempUser.copy(dni = it) },
                label = { Text("DNI") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = coloresOutlined

            )

            OutlinedTextField(
                value = tempUser.codigoPostal,
                onValueChange = { tempUser = tempUser.copy(codigoPostal = it) },
                label = { Text("Código Postal") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = coloresOutlined

            )

            Button(
                onClick = {
                    user = tempUser // Guardamos los cambios en el objeto real
                    isEditing = false
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6332))
            ) {
                Text("GUARDAR CAMBIOS")
            }

        } else {
            // --- VISTA DE LECTURA (Tus items originales) ---
            InfoPerfilItem(Icons.Default.AccountCircle, "Nombre", user.nombre)
            InfoPerfilItem(Icons.Default.Phone, "Teléfono", user.telefono)
            InfoPerfilItem(Icons.Default.Face, "DNI", user.dni)
            InfoPerfilItem(Icons.Default.LocationOn, "Código Postal", user.codigoPostal)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PerfilHeader(
        menuExpanded: Boolean,
        onDismiss: () -> Unit,
        onDeleteClick: () -> Unit,
        navController: NavController)
{

    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp) // Ajuste según necesidad
    ) {
        // Imagen de Portada (Paisaje)
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
            contentScale = ContentScale.Crop
        )

        // Botones sobre la portada (Atrás y Menú)
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón Atrás
            Icon(Icons.AutoMirrored.Filled.ArrowBack,
                "Atrás",
                tint = Color.White,
                modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape)
                    .padding(8.dp)
                    .clickable { navController.popBackStack() })

            // Contenedor para el Menú
            Box {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = Color.White,
                    modifier = Modifier
                        .background(Color.Black.copy(0.3f), CircleShape)
                        .clickable { menuExpanded = true } // Al pulsar, abrimos el menú
                        .padding(8.dp)
                )

                // El menú de image_adde5f.png
                MaterialTheme(
                    // Forzamos un esquema de color similar al verde clarito de tu imagen
                    colorScheme = MaterialTheme.colorScheme.copy(surface = Color(0xFFC5E1A5))
                ) {
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color(0xFFC5E1A5), RoundedCornerShape(16.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Eliminar Cuenta") },
                            leadingIcon = { Icon(Icons.Default.Close, contentDescription = "Eliminar cuenta", tint = Color(0xFF4B6332)) },
                            onClick = {
                                onDeleteClick() // Ejecutamos la función
                                onDismiss()     // Cerramos el menú
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Enviar Reporte", color = Color(0xFF4B6332)) },
                            leadingIcon = { Icon(painterResource(id = R.drawable.bug), contentDescription = "Enviar reporte", tint = Color(0xFF4B6332)) },
                            onClick = {
                                onDismiss()
                                navController.navigate(Screens.Reporte.route) // O la ruta que hayas definido
                            }
                        )
                    }
                }
            }
        }

        // Foto de Perfil Circular
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomCenter)
                .border(4.dp, Color(0xFFF1F4E8), CircleShape) // El color crema de fondo
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun InfoPerfilItem(icon: ImageVector, label: String, value: String, showArrow: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4B6332), // Tu verde
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = value, fontSize = 14.sp, color = Color.Gray)
        }
        if (showArrow) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFF4B6332))
        }
    }
}

@Composable
fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color(0xFFFFEBEE), // Fondo rosado claro de image_a3d734.png
        modifier = Modifier.border(2.dp, Color(0xFFB71C1C), RoundedCornerShape(20.dp)), // Borde rojo
        title = null,
        text = {
            Text(
                text = "¿Seguro desea borrar la cuenta?",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF8B0000), // Texto granate
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)), // Botón Sí rojo
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.width(100.dp)
            ) {
                Text("Sí", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Botón No blanco
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.width(100.dp)
            ) {
                Text("No", color = Color(0xFFB71C1C))
            }
        }
    )
}