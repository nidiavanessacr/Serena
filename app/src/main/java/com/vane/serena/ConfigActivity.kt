package com.vane.serena

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.vane.serena.network.AddLedBody
import com.vane.serena.network.DescriptionBody
import com.vane.serena.network.RetrofitClient

class ConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        DynamicColors.applyToActivitiesIfAvailable(application)
        super.onCreate(savedInstanceState)

        // ============================================================
        // 🔐 PROTEGER LA ACTIVIDAD (solo si hay sesión)
        // ============================================================
        val prefs = getSharedPreferences("serena_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_config)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ============================================================
        // ⭐ MENSAJE DE BIENVENIDA — seguro contra null
        // ============================================================
        val username = prefs.getString("username", null)
        val txtBienvenida = findViewById<TextView>(R.id.txtBienvenida)
        txtBienvenida.text = "Bienvenido, ${username ?: "Usuario"} 👋"

        // ============================================================
        // REFERENCIAS
        // ============================================================
        val inputAddId = findViewById<EditText>(R.id.inputAddId)
        val inputAddDesc = findViewById<EditText>(R.id.inputAddDesc)
        val btnAddLed = findViewById<Button>(R.id.btnAddLed)

        val inputEditId = findViewById<EditText>(R.id.inputEditId)
        val inputEditDesc = findViewById<EditText>(R.id.inputEditDesc)
        val btnEditLed = findViewById<Button>(R.id.btnEditLed)

        val inputDeleteId = findViewById<EditText>(R.id.inputDeleteId)
        val btnDeleteLed = findViewById<Button>(R.id.btnDeleteLed)

        val btnLogoutAdmin = findViewById<Button>(R.id.btnLogoutAdmin)

        // ============================================================
        // 🔴 LOGOUT
        // ============================================================
        btnLogoutAdmin.setOnClickListener {
            prefs.edit().clear().apply()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // ============================================================
        // ➕ AGREGAR LED
        // ============================================================
        btnAddLed.setOnClickListener {
            val id = inputAddId.text.toString().toIntOrNull()
            val desc = inputAddDesc.text.toString()

            if (id == null || desc.isBlank()) {
                showToast("Completa todos los campos para agregar un LED")
                return@setOnClickListener
            }

            agregarLED(id, desc)
        }

        // ============================================================
        // ✏️ EDITAR LED
        // ============================================================
        btnEditLed.setOnClickListener {
            val id = inputEditId.text.toString().toIntOrNull()
            val desc = inputEditDesc.text.toString()

            if (id == null || desc.isBlank()) {
                showToast("Ingresa un ID y una descripción válida")
                return@setOnClickListener
            }

            editarLED(id, desc)
        }

        // ============================================================
        // 🗑 ELIMINAR LED
        // ============================================================
        btnDeleteLed.setOnClickListener {
            val id = inputDeleteId.text.toString().toIntOrNull()

            if (id == null) {
                showToast("Ingresa un ID válido para eliminar")
                return@setOnClickListener
            }

            eliminarLED(id)
        }
    }

    // ============================================================
    // FLECHA DE REGRESO
    // ============================================================
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // ============================================================
    // 🟢 API: AGREGAR LED (ahora null-safe)
    // ============================================================
    private fun agregarLED(id: Int, descripcion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val body = AddLedBody(id, descripcion, false)
                val response = api.addLed(body)

                runOnUiThread {
                    if (response.isSuccessful && response.body() != null) {
                        showToast(response.body()?.mensaje ?: "LED agregado correctamente")
                    } else {
                        showToast("Error: No se pudo agregar el LED")
                    }
                }

            } catch (e: Exception) {
                runOnUiThread { showToast("Error al agregar LED: ${e.message}") }
            }
        }
    }

    // ============================================================
    // 🟡 API: EDITAR LED (ahora null-safe)
    // ============================================================
    private fun editarLED(id: Int, descripcion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val response = api.updateDescription(id, DescriptionBody(descripcion))

                runOnUiThread {
                    if (response.isSuccessful && response.body() != null) {
                        showToast(response.body()?.mensaje ?: "Descripción actualizada")
                    } else {
                        showToast("Error al actualizar la descripción")
                    }
                }

            } catch (e: Exception) {
                runOnUiThread { showToast("Error al editar LED: ${e.message}") }
            }
        }
    }

    // ============================================================
    // 🔴 API: ELIMINAR LED (ahora null-safe)
    // ============================================================
    private fun eliminarLED(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val response = api.deleteLed(id)

                runOnUiThread {
                    if (response.isSuccessful && response.body() != null) {
                        showToast(response.body()?.mensaje ?: "LED eliminado correctamente")
                    } else {
                        showToast("Error al eliminar LED")
                    }
                }

            } catch (e: Exception) {
                runOnUiThread { showToast("Error al eliminar LED: ${e.message}") }
            }
        }
    }

    // ============================================================
    // 🟦 UTILIDAD
    // ============================================================
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
