package com.iremazrod.appmovile.data.network

import com.google.gson.annotations.SerializedName

// --- 1. CLASES GENERALES (REUTILIZABLES) ---

data class GeneralResponse(
    val mensaje: String? = null,
    val error: String? = null,
    val estado: Boolean? = null // Usado en el toggle de favoritos
)

// --- 2. AUTENTICACIÓN (LOGIN/REGISTRO) ---

data class LoginRequest(
    val email: String,
    val contra: String
)

data class LoginResponse(
    val mensaje: String,
    val usuario: UsuarioData
)

data class UsuarioData(
    val id: Int,
    val nickname: String,
    val rol_id: Int,
    val email: String
)

// --- 3. PERFIL DE USUARIO Y ESTADÍSTICAS ---

data class UserProfileResponse(
    val id: Int,
    val nickname: String,
    val nombre: String?, // Añadido
    val email: String?,  // Añadido
    val dni: String?,    // Añadido
    @SerializedName("codigo_postal") val codigo_postal: String?, // Añadido
    val icono: String?,
    @SerializedName("rol_id") val rol_id: Int?, // Añadido
    val estadisticas: UserStats,
    val lista_deseados: List<ParqueResumen>,
    val lista_visitados: List<ParqueResumen>
)

data class UserStats(
    val lista_deseos: Int,
    val parques_visitados: Int
)

data class ParqueResumen(
    val id: Int,
    val nombre: String,
    val img: String,
    val ubicacion: String
)

// --- 4. PARQUES Y BÚSQUEDA ---

data class ParqueResponse(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val img: String,
    val tamanio: String,
    val lat: String,
    val lon: String
)

data class ParqueDetalleResponse(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val tamanio: String,
    val img: String,
    val lat: String,
    val lon: String,
    val es_favorito: Boolean,
    val animales: List<AnimalResponse>
)

data class AnimalResponse(
    val id: Int,
    val nombre: String
)

data class SearchResponse(
    val tipo: String, // "parque" o "ruta"
    val nombre: String,
    val url: String
)

// --- 5. RUTAS ---

data class RutaResponse(
    val id: Int,
    val nombre: String,
    val dificultad: String,
    val web: String? = null,
    val parque_nombre: String? = null // Solo viene en get_all_routes
)

// --- 6. INTERACCIONES (Comentarios / Incidencias) ---

data class ComentarioResponse(
    val id: Int,
    val contenido: String,
    val fecha: String,
    val autor: String? = null,  // Para el detalle del parque
    val avatar: String? = null, // Para el detalle del parque
    val parque_nombre: String? = null // Para el perfil del usuario
)

data class PostCommentRequest(
    val contenido: String,
    val id_usuario: Int,
    val id_parque: Int
)

data class ToggleFavoritoRequest(
    val id_usuario: Int,
    val id_parque: Int
)

data class IncidentRequest(
    val descripcion: String,
    val id_usuario: Int
)

data class FavoritoToggleRequest(
    val id_usuario: Int,
    val id_parque: Int
)

// También crea esta para recibir la respuesta
data class FavoritoToggleResponse(
    val mensaje: String,
    val estado: Boolean
)

// --- 7. ADMINISTRACIÓN ---

data class CreateParkRequest(
    val nombre: String,
    val ubicacion: String,
    val tamanio: String,
    val descripcion: String?,
    val rol_id: Int // Requerido para verificar_admin
)