package com.vane.serena.network

import retrofit2.Response
import retrofit2.http.*

/* ============================================================
   🌐 INTERFAZ PARA COMUNICACIÓN CON LA API (RETROFIT)
   ============================================================ */

interface ApiService {

    // -----------------------------------------------------------
    // 🔍 OBTENER TODOS LOS LEDS
    // -----------------------------------------------------------
    @GET("/leds")
    suspend fun getAllLeds(): Response<LedsResponse>


    // -----------------------------------------------------------
    // 💡 CAMBIAR ESTADO (ON/OFF)
    // -----------------------------------------------------------
    @PUT("/leds/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body body: StatusBody
    ): Response<GenericResponse>


    // -----------------------------------------------------------
    // ➕ AGREGAR LED NUEVO
    // -----------------------------------------------------------
    @POST("/leds")
    suspend fun addLed(
        @Body body: AddLedBody
    ): Response<GenericResponse>


    // -----------------------------------------------------------
    // ✏️ EDITAR DESCRIPCIÓN DE LED
    // -----------------------------------------------------------
    @PUT("/leds/{id}")
    suspend fun updateDescription(
        @Path("id") id: Int,
        @Body body: DescriptionBody
    ): Response<GenericResponse>


    // -----------------------------------------------------------
    // 🗑️ ELIMINAR LED
    // -----------------------------------------------------------
    @DELETE("/leds/{id}")
    suspend fun deleteLed(
        @Path("id") id: Int
    ): Response<GenericResponse>
}


/* ============================================================
   📦 MODELOS DE DATOS (REQUESTS & RESPONSES)
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
