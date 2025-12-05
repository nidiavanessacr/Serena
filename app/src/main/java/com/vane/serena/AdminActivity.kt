package com.vane.serena

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.vane.serena.network.AddLedBody
import com.vane.serena.network.DescriptionBody
import com.vane.serena.network.RetrofitClient

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        DynamicColors.applyToActivitiesIfAvailable(application)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity)

        // Activar flecha de regreso en ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // -----------------------------
        // REFERENCIAS
        // -----------------------------
        val inputAddId = findViewById<EditText>(R.id.inputAddId)
        val inputAddDesc = findViewById<EditText>(R.id.inputAddDesc)
        val btnAddLed = findViewById<Button>(R.id.btnAddLed)

        val inputEditId = findViewById<EditText>(R.id.inputEditId)
        val inputEditDesc = findViewById<EditText>(R.id.inputEditDesc)
        val btnEditLed = findViewById<Button>(R.id.btnEditLed)

        val inputDeleteId = findViewById<EditText>(R.id.inputDeleteId)
        val btnDeleteLed = findViewById<Button>(R.id.btnDeleteLed)

        // -----------------------------
        // AGREGAR LED
        // -----------------------------
        btnAddLed.setOnClickListener {
            val id = inputAddId.text.toString().toIntOrNull()
            val desc = inputAddDesc.text.toString()

            if (id == null || desc.isBlank()) {
                showToast("Completa todos los campos para agregar un LED")
                return@setOnClickListener
            }

            agregarLED(id, desc)
        }

        // -----------------------------
        // EDITAR LED
        // -----------------------------
        btnEditLed.setOnClickListener {
            val id = inputEditId.text.toString().toIntOrNull()
            val desc = inputEditDesc.text.toString()

            if (id == null || desc.isBlank()) {
                showToast("Ingresa un ID y una descripción válida")
                return@setOnClickListener
            }

            editarLED(id, desc)
        }

        // -----------------------------
        // ELIMINAR LED
        // -----------------------------
        btnDeleteLed.setOnClickListener {
            val id = inputDeleteId.text.toString().toIntOrNull()

            if (id == null) {
                showToast("Ingresa un ID válido para eliminar")
                return@setOnClickListener
            }

            eliminarLED(id)
        }
    }

    // Habilitar botón de regresar (flecha)
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // ============================================================
    // FUNCIONES DE API
    // ============================================================

    private fun agregarLED(id: Int, descripcion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val body = AddLedBody(id, descripcion, false)
                val response = api.addLed(body)

                runOnUiThread {
                    if (response.isSuccessful)
                        showToast(response.body()?.mensaje ?: "LED agregado correctamente")
                    else
                        showToast("Error: No se pudo agregar el LED")
                }

            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Error al agregar LED: ${e.message}")
                }
            }
        }
    }

    private fun editarLED(id: Int, descripcion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val response = api.updateDescription(id, DescriptionBody(descripcion))

                runOnUiThread {
                    if (response.isSuccessful)
                        showToast(response.body()?.mensaje ?: "Descripción actualizada")
                    else
                        showToast("Error al actualizar la descripción")
                }

            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Error al editar LED: ${e.message}")
                }
            }
        }
    }

    private fun eliminarLED(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val response = api.deleteLed(id)

                runOnUiThread {
                    if (response.isSuccessful)
                        showToast(response.body()?.mensaje ?: "LED eliminado")
                    else
                        showToast("Error al eliminar LED")
                }

            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Error al eliminar LED: ${e.message}")
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
