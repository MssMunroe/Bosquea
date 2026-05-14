package com.iremazrod.appmovile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iremazrod.appmovile.pantallas.Inicio
import com.iremazrod.appmovile.pantallas.Buscar
import com.iremazrod.appmovile.pantallas.Favoritos
import com.iremazrod.appmovile.pantallas.LoginScreen
import com.iremazrod.appmovile.pantallas.Perfil
import com.iremazrod.appmovile.pantallas.Mapa
import com.iremazrod.appmovile.pantallas.Parque
import com.iremazrod.appmovile.pantallas.RegistroScreen
import com.iremazrod.appmovile.pantallas.Reporte
import com.iremazrod.appmovile.pantallas.Rutas
import com.iremazrod.appmovile.ui.theme.AppmovileTheme
import com.iremazrod.appmovile.ui.theme.BosqueaBottomBar
import com.iremazrod.appmovile.ui.theme.BosqueaTopBar
import com.iremazrod.appmovile.ui.theme.Screens
import com.iremazrod.appmovile.ui.theme.SplashScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  //Para respetar las zonas del movil
        setContent {
            AppmovileTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Definimos dónde se ve cada cosa
                val showTopBar = currentRoute in listOf(
                    Screens.Inicio.route,
                    Screens.Busqueda.route,
                    Screens.Mapa.route,
                    Screens.Rutas.route
                )

                val showBottomBar = currentRoute in listOf(
                    Screens.Inicio.route,
                    Screens.Busqueda.route,
                    Screens.Mapa.route,
                    Screens.Rutas.route,
                    Screens.Perfil.route // El Nav también se ve en Perfil
                )

                Scaffold(
                    topBar = {
                        if (showTopBar) {
                            BosqueaTopBar(navController)
                        }
                    },
                    bottomBar = {
                        if (showBottomBar) {
                            BosqueaBottomBar(navController)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = Screens.Splash.route,
                        ) {
                            composable(Screens.Splash.route) { SplashScreen(navController) }
                            composable(Screens.Login.route) { LoginScreen(navController) }
                            composable(Screens.Registro.route) { RegistroScreen(navController) }
                            composable(Screens.Inicio.route) { Inicio() }
                            composable(Screens.Busqueda.route) { Buscar() }
                            composable(Screens.Mapa.route) { Mapa(navController) }
                            composable(Screens.Rutas.route) { Rutas() }
                            composable(Screens.Perfil.route) { Perfil(navController) }
                            composable(Screens.Favoritos.route) { Favoritos(navController) }
                            composable(Screens.Reporte.route) { Reporte(navController) }
                            composable(Screens.Parque.route) { Parque("Sierra Nevada") }
                        }
                    }

                }
            }
        }
    }
}

