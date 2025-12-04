package com.vane.serena.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

// Interfaz que define cómo se hacen las peticiones HTTP
interface ApiService {
    @GET
    suspend fun getLedStatus(@Url url: String): Map<String, Any>
}

// Singleton para inicializar Retrofit
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.76:5000/"  // Cambia según tu IP Flask

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
