package com.iremazrod.appmovile.data.network

import com.google.gson.annotations.SerializedName

// --- CLASES GENERALES ---

data class GeneralResponse(
    val mensaje: String? = null,
    val error: String? = null,
    val estado: Boolean? = null
)

// --- AUTENTICACIÓN ---

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

// --- PERFIL DE USUARIO ---

data class UserProfileResponse(
    val id: Int,
    val nickname: String,
    val nombre: String?,
    val email: String?,
    val dni: String?,
    val telefono: String?,
    @SerializedName("codigo_postal") val codigo_postal: String?,
    val icono: String?,
    @SerializedName("rol_id") val rol_id: Int?,
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

data class UpdateProfileRequest(
    val nombre: String? = null,
    val nickname: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val contra: String? = null,
    val icono: String? = null
)

// --- PARQUES Y BÚSQUEDA ---

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

data class AnimalResponse(
    val id: Int,
    val nombre: String
)

data class SearchResponse(
    val tipo: String,
    val nombre: String,
    val url: String
)

// --- RUTAS ---

data class RutaResponse(
    val id: Int,
    val nombre: String,
    val dificultad: String,
    val web: String? = null,
    val parque_nombre: String? = null
)

// --- INTERACCIONES ---

data class ComentarioResponse(
    val id: Int,
    val contenido: String,
    val fecha: String,
    val autor: String? = null,
    val avatar: String? = null,
    val parque_nombre: String? = null
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

// para recibir la respuesta
data class FavoritoToggleResponse(
    val mensaje: String,
    val estado: Boolean
)

// --- ADMINISTRACIÓN ---

data class CreateParkRequest(
    val nombre: String,
    val ubicacion: String,
    val tamanio: String,
    val descripcion: String?,
    val rol_id: Int
)