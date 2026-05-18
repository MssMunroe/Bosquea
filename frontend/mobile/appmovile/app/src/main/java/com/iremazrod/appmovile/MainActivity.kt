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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iremazrod.appmovile.pantallas.*
import com.iremazrod.appmovile.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración para OpenStreetMap
        org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName

        // Habilita el diseño de borde a borde
        enableEdgeToEdge()

        setContent {
            AppmovileTheme {
                val navController = rememberNavController()

                // Ruta actual
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Definimos en qué pantallas se ven las barras
                val showTopBar = currentRoute in listOf(
                    Screens.Inicio.route,
                    Screens.Busqueda.route,
                    Screens.Mapa.route,
                    Screens.Rutas.route,
                    Screens.Favoritos.route
                )

                val showBottomBar = currentRoute in listOf(
                    Screens.Inicio.route,
                    Screens.Busqueda.route,
                    Screens.Mapa.route,
                    Screens.Rutas.route,
                    Screens.Perfil.route,
                    Screens.Favoritos.route
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
                    // El Box con innerPadding para evitar que se oculte tras las barras
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = Screens.Splash.route,
                        ) {
                            // Pantallas Iniciales
                            composable(Screens.Splash.route) { SplashScreen(navController) }
                            composable(Screens.Login.route) { LoginScreen(navController) }
                            composable(Screens.Registro.route) { RegistroScreen(navController) }

                            // Pantallas Principales
                            composable(Screens.Inicio.route) { Inicio(navController) }
                            composable(Screens.Busqueda.route) { Buscar(navController) }
                            composable(Screens.Mapa.route) { Mapa(navController) }
                            composable(Screens.Rutas.route) { Rutas() }

                            // Pantallas Usuario
                            composable(Screens.Perfil.route) { Perfil(navController) }
                            composable(Screens.Favoritos.route) { Favoritos(navController) }
                            composable(Screens.Reporte.route) { Reporte(navController) }

                            // Pantalla Parque
                            composable(
                                route = "parque/{parqueNombre}",
                                arguments = listOf(navArgument("parqueNombre") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val nombre = backStackEntry.arguments?.getString("parqueNombre") ?: ""
                                Parque(nombre, navController) // Pasamos el nombre y el controller
                            }
                        }
                    }
                }
            }
        }
    }
}