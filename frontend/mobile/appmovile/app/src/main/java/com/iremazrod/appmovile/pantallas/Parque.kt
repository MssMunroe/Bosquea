package com.iremazrod.appmovile.pantallas

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.data.network.ParqueResponse
import com.iremazrod.appmovile.data.network.RetrofitClient
import com.iremazrod.appmovile.data.network.FavoritoToggleRequest
import com.iremazrod.appmovile.data.network.ComentarioResponse
import com.iremazrod.appmovile.data.network.PostCommentRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Parque(nombreParque: String, navController: NavController) {
    var parque: ParqueResponse? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }

    // --- NUEVOS ESTADOS PARA COMENTARIOS ---
    var listaComentarios by remember { mutableStateOf<List<ComentarioResponse>>(emptyList()) }
    var nuevoComentarioTexto by remember { mutableStateOf("") }
    var enviandoComentario by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var esFavorito by remember { mutableStateOf(false) }
    var esVisitado by remember { mutableStateOf(false) }

    val userId = context.getSharedPreferences("auth", Context.MODE_PRIVATE).getInt("userId", -1)

    // Función auxiliar para recargar únicamente los comentarios
    fun cargarComentarios(idParque: Int) {
        scope.launch {
            try {
                val comentarios = RetrofitClient.instance.getComments(idParque)
                listaComentarios = comentarios
            } catch (e: Exception) {
                Log.e("PARQUE_COMENTARIOS", "Error al traer comentarios: ${e.message}")
            }
        }
    }

    LaunchedEffect(nombreParque) {
        try {
            val p = RetrofitClient.instance.getParqueDetalle(nombreParque)
            parque = p

            // Si el parque se cargó correctamente, traemos sus comentarios correspondientes
            val comentarios = RetrofitClient.instance.getComments(p.id)
            listaComentarios = comentarios
        } catch (e: Exception) {
            Log.e("PARQUE_DETALLE", "Error: ${e.message}")
            Toast.makeText(context, "No se pudo cargar la información del parque", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color(0xFF4B6332))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF1F4E8)
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4B6332))
            }
        } else if (parque != null) {
            val p = parque!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF1F4E8))
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = p.nombre.uppercase(),
                    fontSize = 26.sp,
                    color = Color(0xFF4B3D21),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp).width(150.dp),
                    thickness = 2.dp,
                    color = Color(0xFF4B6332)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionIconButton(
                        icon = if (esVisitado) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        label = if (esVisitado) "Visitado" else "Marcar visita",
                        color = if (esVisitado) Color(0xFF4B6332) else Color.Gray,
                        onClick = { esVisitado = !esVisitado }
                    )

                    Spacer(modifier = Modifier.width(40.dp))

                    ActionIconButton(
                        icon = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        label = "Favorito",
                        color = if (esFavorito) Color.Red else Color.Gray,
                        onClick = {
                            if (userId == -1) {
                                Toast.makeText(context, "Inicia sesión para guardar favoritos", Toast.LENGTH_SHORT).show()
                                return@ActionIconButton
                            }
                            scope.launch {
                                try {
                                    val request = FavoritoToggleRequest(id_usuario = userId, id_parque = p.id)
                                    val response = RetrofitClient.instance.toggleFavorito(request)
                                    if (response.isSuccessful) {
                                        esFavorito = response.body()?.estado ?: !esFavorito
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "{$e} Error al actualizar favoritos", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }

                AsyncImage(
                    model = p.img,
                    contentDescription = p.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.logo),
                    error = painterResource(R.drawable.logo)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = p.descripcion,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify,
                        color = Color(0xFF2C2C2C),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EDDA)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("FICHA TÉCNICA", fontWeight = FontWeight.ExtraBold, color = Color(0xFF4B6332), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        TechRow("Superficie", p.tamanio)
                        TechRow("Ubicación", p.ubicacion)
                        TechRow("Coordenadas", "${p.lat}, ${p.lon}")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ==========================================
                // SECCIÓN NUEVA: COMENTARIOS Y APORTACIONES
                // ==========================================
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "COMUNIDAD Y OPINIONES",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4B3D21),
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Formulario para añadir nuevo comentario
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = nuevoComentarioTexto,
                                onValueChange = { nuevoComentarioTexto = it },
                                placeholder = { Text("Escribe tu experiencia en el parque...", fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                maxLines = 3,
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4B6332),
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                enabled = !enviandoComentario
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (userId == -1) {
                                        Toast.makeText(context, "Inicia sesión para poder comentar", Toast.LENGTH_SHORT).show()
                                        return@IconButton
                                    }
                                    if (nuevoComentarioTexto.isBlank()) return@IconButton

                                    scope.launch {
                                        enviandoComentario = true
                                        try {
                                            val req = PostCommentRequest(
                                                contenido = nuevoComentarioTexto.trim(),
                                                id_usuario = userId,
                                                id_parque = p.id
                                            )
                                            val res = RetrofitClient.instance.postComment(req)
                                            if (res.isSuccessful) {
                                                nuevoComentarioTexto = ""
                                                Toast.makeText(context, "¡Comentario publicado!", Toast.LENGTH_SHORT).show()
                                                // Recargamos la lista localmente
                                                cargarComentarios(p.id)
                                            } else {
                                                Toast.makeText(context, "No se pudo enviar el comentario", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "{$e} Error de red al comentar", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            enviandoComentario = false
                                        }
                                    }
                                },
                                enabled = nuevoComentarioTexto.isNotBlank() && !enviandoComentario,
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = Color(0xFF4B6332),
                                    disabledContentColor = Color.Gray
                                )
                            ) {
                                if (enviandoComentario) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFF4B6332))
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Feed de comentarios dinámicos
                    if (listaComentarios.isEmpty()) {
                        Text(
                            text = "Aún no hay comentarios. ¡Sé el primero en compartir tu opinión!",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    } else {
                        listaComentarios.forEach { comentario ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Avatar o iniciales del autor
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Usuario",
                                        tint = Color(0xFF4B6332),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = comentario.autor ?: "Explorador",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF4B3D21)
                                            )
                                            Text(
                                                text = comentario.fecha.take(10), // Simplifica la cadena de fecha a YYYY-MM-DD
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = comentario.contenido,
                                            fontSize = 13.sp,
                                            color = Color(0xFF333333),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
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
fun ActionIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(30.dp))
        }
        Text(text = label, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TechRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$label: ", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF4B3D21))
        Text(text = value, fontSize = 14.sp, color = Color.Black)
    }
}