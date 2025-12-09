package com.vane.serena.network

import retrofit2.Response
import retrofit2.http.*

// MODELOS DE LOGIN Y REGISTRO
import com.vane.serena.network.LoginBody
import com.vane.serena.network.LoginResponse
import com.vane.serena.network.RegisterBody
import com.vane.serena.network.RegisterResponse

/* ============================================================
   INTERFAZ DE LA API PARA RETROFIT
   ============================================================ */
interface ApiService {

    // LEDS

    @GET("/leds")
    suspend fun getAllLeds(): Response<LedsResponse>

    @PUT("/leds/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body body: StatusBody
    ): Response<GenericResponse>

    @POST("/leds")
    suspend fun addLed(
        @Body body: AddLedBody
    ): Response<GenericResponse>

    @PUT("/leds/{id}")
    suspend fun updateDescription(
        @Path("id") id: Int,
        @Body body: DescriptionBody
    ): Response<GenericResponse>

    @DELETE("/leds/{id}")
    suspend fun deleteLed(
        @Path("id") id: Int
    ): Response<GenericResponse>

    // LOGIN

    @POST("/login")
    suspend fun loginUser(
        @Body body: LoginBody
    ): Response<LoginResponse>

    // REGISTRO

    @POST("/register")
    suspend fun registerUser(
        @Body body: RegisterBody
    ): Response<RegisterResponse>
}

/* ============================================================
   MODELOS PARA LEDS
   ============================================================ */

data class LedsResponse(
    val leds: List<LedItem>,
    val mensaje: String
)

data class LedItem(
    val id: Int,
    val descripcion: String,
    val status: Boolean
)

data class StatusBody(
    val status: Boolean
)

data class AddLedBody(
    val id: Int,
    val descripcion: String,
    val status: Boolean
)

data class DescriptionBody(
    val descripcion: String
)

data class GenericResponse(
    val mensaje: String
)
