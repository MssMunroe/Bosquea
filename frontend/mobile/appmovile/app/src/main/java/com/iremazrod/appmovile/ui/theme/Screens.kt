package com.iremazrod.appmovile.ui.theme

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Login : Screens("login")
    object Registro : Screens("registro")
    object Inicio : Screens("inicio")
    object Busqueda : Screens("busqueda")
    object Mapa : Screens("mapa")
    object Rutas : Screens("rutas")
    object Perfil : Screens("perfil")
    object Favoritos : Screens("favoritos")
    object Reporte : Screens("reporte")
    object Parque : Screens("parque")


}