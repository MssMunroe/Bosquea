package com.iremazrod.appmovile.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Lo que envías a Flask

interface BosqueaApiService {

    // @app.route('/api/auth/login', methods=['POST'])
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // @app.route('/api/parques', methods=['GET'])
    @GET("api/parques")
    suspend fun getParques(): List<ParqueResponse>

    // @app.route('/api/users/<int:id>/profile', methods=['GET'])
    @GET("api/users/{id}/profile")
    suspend fun getUserProfile(@Path("id") userId: Int): UserProfileResponse
}