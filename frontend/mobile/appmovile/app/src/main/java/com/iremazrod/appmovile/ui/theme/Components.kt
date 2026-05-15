package com.iremazrod.appmovile.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.iremazrod.appmovile.R
import kotlinx.coroutines.delay

// --- SPLASH SCREEN ---
@Composable
fun SplashScreen(navController: NavController) {
    // Efecto de lanzamiento: Redirige al Login tras 2 segundos
    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.navigate(Screens.Login.route) {
            // Limpiamos el Splash de la pila para que el usuario no pueda volver atrás
            popUpTo(Screens.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8)), // Fondo crema claro
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BOSQUEA",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B6332), // Verde corporativo
                    letterSpacing = 4.sp
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Bosquea",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}

// --- BOTTOM NAVIGATION BAR ---
@Composable
fun BosqueaBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Definimos los items de navegación
        val items = listOf(
            Triple(Screens.Inicio.route, Icons.Default.Home, "Inicio"),
            Triple(Screens.Busqueda.route, Icons.Default.Search, "Buscar"),
            Triple(Screens.Mapa.route, R.drawable.map, "Mapa"),
            Triple(Screens.Rutas.route, R.drawable.ruta, "Rutas")
        )

        items.forEach { (route, icon, label) ->
            NavigationBarItem(
                icon = {
                    if (icon is Int) {
                        // Para iconos personalizados desde res/drawable
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        // Para iconos predeterminados de Material
                        Icon(icon as androidx.compose.ui.graphics.vector.ImageVector, contentDescription = label)
                    }
                },
                selected = currentRoute == route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4B6332),
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFFF1F4E8)
                ),
                onClick = {
                    // Navegación optimizada: evita duplicar pantallas en la pila
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

// --- TOP BAR ---
@Composable
fun BosqueaTopBar(navController: NavController) {
    Surface(
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Evita que el contenido quede bajo la barra de hora/batería
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono Perfil (Izquierda)
            IconButton(onClick = { navController.navigate(Screens.Perfil.route) }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    tint = Color(0xFF4B6332),
                    modifier = Modifier.size(32.dp)
                )
            }

            // Logo Central (Clicable para volver al inicio rápido)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
            ) {
                IconButton(onClick = {
                    navController.navigate(Screens.Inicio.route) {
                        popUpTo(Screens.Inicio.route) { inclusive = true }
                    }
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Inicio"
                    )
                }
            }

            // Icono Favoritos (Derecha)
            IconButton(onClick = { navController.navigate(Screens.Favoritos.route) }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favoritos",
                    tint = Color(0xFF4B6332),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}