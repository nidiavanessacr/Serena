package com.vane.serena

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.vane.serena.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // IDs REALES según la BD MySQL
    private val ID_ROJO = 1
    private val ID_VERDE = 2
    private val ID_AZUL = 3

    override fun onCreate(savedInstanceState: Bundle?) {

        // 🌈 ACTIVAR MATERIAL YOU
        DynamicColors.applyToActivitiesIfAvailable(application)

        super.onCreate(savedInstanceState)

        // 🔒 PROTEGER PANTALLA — si NO hay sesión → volver a login
        verificarSesion()

        setContentView(R.layout.activity_main)

        // -----------------------------
        // BOTÓN DE CERRAR SESIÓN
        -----------------------------
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener { cerrarSesion() }

        // -----------------------------
        // REFERENCIAS A BOTONES
        -----------------------------
        val btnRojoOn = findViewById<Button>(R.id.btnRojoOn)
        val btnRojoOff = findViewById<Button>(R.id.btnRojoOff)

        val btnVerdeOn = findViewById<Button>(R.id.btnVerdeOn)
        val btnVerdeOff = findViewById<Button>(R.id.btnVerdeOff)

        val btnAzulOn = findViewById<Button>(R.id.btnAzulOn)
        val btnAzulOff = findViewById<Button>(R.id.btnAzulOff)

        val btnApagarTodo = findViewById<Button>(R.id.btnApagar)
        val txtEstado = findViewById<TextView>(R.id.txtEstado)

        // EVENTOS
        btnRojoOn.setOnClickListener { cambiarEstadoLED(ID_ROJO, true, txtEstado) }
        btnRojoOff.setOnClickListener { cambiarEstadoLED(ID_ROJO, false, txtEstado) }

        btnVerdeOn.setOnClickListener { cambiarEstadoLED(ID_VERDE, true, txtEstado) }
        btnVerdeOff.setOnClickListener { cambiarEstadoLED(ID_VERDE, false, txtEstado) }

        btnAzulOn.setOnClickListener { cambiarEstadoLED(ID_AZUL, true, txtEstado) }
        btnAzulOff.setOnClickListener { cambiarEstadoLED(ID_AZUL, false, txtEstado) }

        btnApagarTodo.setOnClickListener { apagarTodos(txtEstado) }

        // Verificar conexión al iniciar
        verificarConexion(txtEstado)
    }

    // =======================================================
    // 🔒 VERIFICAR SESIÓN
    // =======================================================
    private fun verificarSesion() {
        val prefs = getSharedPreferences("serena_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        if (userId == -1) {
            // Nadie inició sesión → regresar al login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // =======================================================
    // 🔘 CERRAR SESIÓN
    // =======================================================
    private fun cerrarSesion() {
        val prefs = getSharedPreferences("serena_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // =======================================================
    // MENÚ SUPERIOR (ENGRANE)
    // =======================================================
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_admin -> {
                startActivity(Intent(this, AdminActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // =======================================================
    // API FLASK
    // =======================================================
    private fun cambiarEstadoLED(id: Int, encender: Boolean, txtEstado: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val response = api.updateStatus(id, StatusBody(encender))

                runOnUiThread {
                    if (response.isSuccessful) {
                        txtEstado.text = "Estado: Conectado ✅"
                        Toast.makeText(this@MainActivity, "LED $id actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        txtEstado.text = "Estado: Error con API ❌"
                        Toast.makeText(this@MainActivity, "Error en la API", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    txtEstado.text = "Estado: Desconectado ❌"
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun apagarTodos(txtEstado: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance

                api.updateStatus(ID_ROJO, StatusBody(false))
                api.updateStatus(ID_VERDE, StatusBody(false))
                api.updateStatus(ID_AZUL, StatusBody(false))

                runOnUiThread {
                    txtEstado.text = "Estado: Conectado ✅"
                    Toast.makeText(this@MainActivity, "Todos los LEDs apagados", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    txtEstado.text = "Estado: Desconectado ❌"
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun verificarConexion(txtEstado: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val response = api.getAllLeds()

                runOnUiThread {
                    if (response.isSuccessful) {
                        txtEstado.text = "Estado: Conectado ✅"
                    } else {
                        txtEstado.text = "Estado: Error al conectar ❌"
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    txtEstado.text = "Estado: Desconectado ❌"
                }
            }
        }
    }
}
