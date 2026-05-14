package com.iremazrod.appmovile.data.network

data class LoginRequest(
    val email: String,
    val contra: String
)

// Lo que Flask responde en el JSON
data class LoginResponse(
    val mensaje: String,
    val usuario: UsuarioData
)

data class UsuarioData(
    val nickname: String,
    val rol_id: Int,
    val email: String
)

data class ParqueResponse(
    val nickname: String,
    val rol_id: Int,
    val email: String
)

data class UserProfileResponse(
    val nickname: String,
    val rol_id: Int,
    val email: String
)