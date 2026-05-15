package com.iremazrod.appmovile.data.network

import retrofit2.Response
import retrofit2.http.*

interface BosqueaApiService {

    // --- 1. AUTENTICACIÓN ---

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @FormUrlEncoded
    @POST("api/auth/register")
    suspend fun register(
        @Field("nombre") nombre: String,
        @Field("nickname") nickname: String,
        @Field("email") email: String,
        @Field("contra") contra: String,
        @Field("dni") dni: String,
        @Field("codigo_postal") cp: String
    ): Response<GeneralResponse>

    // --- 2. PERFIL Y USUARIOS ---

    @GET("api/users/{id}/profile")
    suspend fun getUserProfile(@Path("id") userId: Int): UserProfileResponse

    @GET("api/users/{id}/comments")
    suspend fun getUserComments(@Path("id") userId: Int): List<ComentarioResponse>

    // --- 3. PARQUES Y BÚSQUEDA ---

    @GET("api/parques")
    suspend fun getParques(): List<ParqueResponse>

    @GET("api/parques/{nombre}")
    suspend fun getParqueDetalle(
        @Path("nombre") nombre: String,
        @Query("user_id") userId: Int? = null
    ): ParqueResponse

    @GET("api/search")
    suspend fun buscar(@Query("q") query: String): List<SearchResponse>

    // --- 4. RUTAS ---

    @GET("api/routes")
    suspend fun getAllRoutes(): List<RutaResponse>

    @GET("api/parques/{id}/routes")
    suspend fun getParkRoutes(@Path("id") parkId: Int): List<RutaResponse>

    // --- 5. INTERACCIONES ---

    @GET("api/parques/{id_parque}/comments")
    suspend fun getComments(@Path("id_parque") parkId: Int): List<ComentarioResponse>

    @POST("api/comments")
    suspend fun postComment(@Body request: PostCommentRequest): Response<GeneralResponse>

    @DELETE("api/comments/{comment_id}")
    suspend fun deleteComment(@Path("comment_id") commentId: Int): Response<GeneralResponse>

    @POST("api/favoritos/toggle")
    suspend fun toggleFavorito(@Body request: FavoritoToggleRequest): Response<GeneralResponse>

    @POST("api/reports/incident")
    suspend fun postIncident(@Body request: IncidentRequest): Response<GeneralResponse>

    // --- 6. ADMINISTRACIÓN (ADMINS ONLY) ---

    @POST("api/admin/parques")
    suspend fun adminCreatePark(@Body request: CreateParkRequest): Response<GeneralResponse>

    @GET("api/admin/report-chart")
    suspend fun exportAdminReport(): Response<Unit> // Descarga de archivo Excel

    @Multipart
    @POST("api/admin/import-xml")
    suspend fun importXml(
        @Part("rol_id") rolId: Int,
        @Part file: Any // Requiere MultipartBody.Part para el archivo XML
    ): Response<GeneralResponse>
}