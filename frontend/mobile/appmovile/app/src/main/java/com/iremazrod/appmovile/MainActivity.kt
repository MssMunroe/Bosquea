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

        // Habilita el diseño de borde a borde (transparencia en barras de estado)
        enableEdgeToEdge()

        setContent {
            AppmovileTheme {
                val navController = rememberNavController()

                // Observamos la ruta actual para decidir qué componentes mostrar
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // LÓGICA DE UI: Definimos en qué pantallas se ven las barras
                val showTopBar = currentRoute in listOf(
                    Screens.Inicio.route,
                    Screens.Busqueda.route,
                    Screens.Mapa.route,
                    Screens.Rutas.route,
                    Screens.Favoritos.route // Añadido: Favoritos suele llevar el logo arriba
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
                    // El Box con innerPadding evita que el contenido se oculte tras las barras
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = Screens.Splash.route,
                        ) {
                            // Pantallas de Flujo Inicial
                            composable(Screens.Splash.route) { SplashScreen(navController) }
                            composable(Screens.Login.route) { LoginScreen(navController) }
                            composable(Screens.Registro.route) { RegistroScreen(navController) }

                            // Pantallas Principales
                            composable(Screens.Inicio.route) { Inicio(navController) }
                            composable(Screens.Busqueda.route) { Buscar(navController) }
                            composable(Screens.Mapa.route) { Mapa(navController) }
                            composable(Screens.Rutas.route) { Rutas() }

                            // Pantallas de Usuario
                            composable(Screens.Perfil.route) { Perfil(navController) }
                            composable(Screens.Favoritos.route) { Favoritos(navController) }
                            composable(Screens.Reporte.route) { Reporte(navController) }

                            // DETALLE DEL PARQUE (CORREGIDO)
                            // Cambiamos parqueId (Int) por parqueNombre (String)
                            // porque el backend 'app.py' busca por nombre en /api/parques/<nombre>
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