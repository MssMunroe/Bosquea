package com.iremazrod.appmovile.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.iremazrod.appmovile.R
import com.iremazrod.appmovile.ui.theme.Screens
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController) {
    // LaunchedEffect sola al entrar en la pantalla
    LaunchedEffect(key1 = true) {
        delay(2000) // Espera 2 seg
        navController.navigate(Screens.Login.route) {
            popUpTo(Screens.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F4E8)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BOSQUEA",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B6332), // Tu verde corporativo
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

@Composable
fun BosqueaBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF4B6332) // El verde de tus iconos
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            selected = currentRoute == Screens.Inicio.route,
            onClick = { navController.navigate(Screens.Inicio.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            selected = currentRoute == Screens.Busqueda.route,
            onClick = { navController.navigate(Screens.Busqueda.route) }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.map), contentDescription = "Mapa") },
            selected = currentRoute == Screens.Mapa.route,
            onClick = { navController.navigate(Screens.Mapa.route) }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ruta), contentDescription = "Rutas") },
            selected = currentRoute == Screens.Rutas.route,
            onClick = { navController.navigate(Screens.Rutas.route) }
        )
    }
}


@Composable
fun BosqueaTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono Perfil
        IconButton(onClick = { navController.navigate(Screens.Perfil.route) }) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = Color(0xFF4B6332))
        }

        // Logo Central
        IconButton(
            onClick = { navController.navigate(Screens.Inicio.route) },
            modifier = Modifier.size(60.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Inicio"
            )
        }

        // Icono Favoritos
        IconButton(onClick = { navController.navigate(Screens.Favoritos.route) }) {
            Icon(Icons.Default.Favorite, contentDescription = "Favoritos", tint = Color(0xFF4B6332))
        }
    }
}


