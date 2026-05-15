package com.iremazrod.appmovile.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 10.0.2.2 es la IP especial para que el emulador de Android vea el localhost de tu PC (Flask)
    private const val BASE_URL = "http://10.0.2.2:5000/"

    // Configuramos un cliente OkHttp para controlar tiempos de espera y logs
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para conectar
        .readTimeout(30, TimeUnit.SECONDS)    // Tiempo máximo para recibir datos
        .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo máximo para enviar datos (útil para subir fotos)
        .build()

    val instance: BosqueaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Añadimos el cliente configurado
            .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a tus Data Classes
            .build()
            .create(BosqueaApiService::class.java)
    }
}