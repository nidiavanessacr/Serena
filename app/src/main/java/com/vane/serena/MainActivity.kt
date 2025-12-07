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

        // 🌈 ACTIVA MATERIAL YOU
        DynamicColors.applyToActivitiesIfAvailable(application)

        super.onCreate(savedInstanceState)

        // 🔒 Verificar sesión
        verificarSesion()

        setContentView(R.layout.activity_main)

        // ============================================================
        // ⭐ MOSTRAR NOMBRE DEL USUARIO
        // ============================================================
        val prefs = getSharedPreferences("serena_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Usuario")

        val txtBienvenida = findViewById<TextView>(R.id.txtBienvenidaMain)
        txtBienvenida.text = getString(R.string.bienvenida, username)

        // ============================================================
        // 🔘 BOTÓN DE CERRAR SESIÓN
        // ============================================================
        findViewById<Button>(R.id.btnLogout).setOnClickListener { cerrarSesion() }

        // ============================================================
        // REFERENCIAS A BOTONES
        // ============================================================
        val btnRojoOn = findViewById<Button>(R.id.btnRojoOn)
        val btnRojoOff = findViewById<Button>(R.id.btnRojoOff)
        val btnVerdeOn = findViewById<Button>(R.id.btnVerdeOn)
        val btnVerdeOff = findViewById<Button>(R.id.btnVerdeOff)
        val btnAzulOn = findViewById<Button>(R.id.btnAzulOn)
        val btnAzulOff = findViewById<Button>(R.id.btnAzulOff)
        val btnApagarTodo = findViewById<Button>(R.id.btnApagar)
        val txtEstado = findViewById<TextView>(R.id.txtEstado)

        // ============================================================
        // EVENTOS
        // ============================================================
        btnRojoOn.setOnClickListener { cambiarEstadoLED(ID_ROJO, true, txtEstado) }
        btnRojoOff.setOnClickListener { cambiarEstadoLED(ID_ROJO, false, txtEstado) }

        btnVerdeOn.setOnClickListener { cambiarEstadoLED(ID_VERDE, true, txtEstado) }
        btnVerdeOff.setOnClickListener { cambiarEstadoLED(ID_VERDE, false, txtEstado) }

        btnAzulOn.setOnClickListener { cambiarEstadoLED(ID_AZUL, true, txtEstado) }
        btnAzulOff.setOnClickListener { cambiarEstadoLED(ID_AZUL, false, txtEstado) }

        btnApagarTodo.setOnClickListener { apagarTodos(txtEstado) }

        verificarConexion(txtEstado)
    }

    // =======================================================
    // 🔒 VERIFICAR SESIÓN
    // =======================================================
    private fun verificarSesion() {
        val prefs = getSharedPreferences("serena_prefs", Context.MODE_PRIVATE)
        if (prefs.getInt("user_id", -1) == -1) {
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
        }
    }

    // =======================================================
    // 🔘 CERRAR SESIÓN
    // =======================================================
    private fun cerrarSesion() {
        val prefs = getSharedPreferences("serena_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        Toast.makeText(this, getString(R.string.logout_btn), Toast.LENGTH_SHORT).show()

        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
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
                startActivity(Intent(this, ConfigActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // =======================================================
    // API FLASK — CAMBIAR ESTADO INDIVIDUAL
    // =======================================================
    private fun cambiarEstadoLED(id: Int, encender: Boolean, txtEstado: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val response = api.updateStatus(id, StatusBody(encender))

                runOnUiThread {
                    if (response.isSuccessful) {
                        txtEstado.text = getString(R.string.estado_conectado)
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.toast_led_actualizado, id),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        txtEstado.text = getString(R.string.estado_error_api)
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.toast_error_api),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    txtEstado.text = getString(R.string.estado_desconectado)
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // =======================================================
    // API FLASK — APAGAR TODOS
    // =======================================================
    private fun apagarTodos(txtEstado: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance

                api.updateStatus(ID_ROJO, StatusBody(false))
                api.updateStatus(ID_VERDE, StatusBody(false))
                api.updateStatus(ID_AZUL, StatusBody(false))

                runOnUiThread {
                    txtEstado.text = getString(R.string.estado_conectado)
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.toast_todos_apagados),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    txtEstado.text = getString(R.string.estado_desconectado)
                }
            }
        }
    }

    // =======================================================
    // API FLASK — VERIFICAR CONEXIÓN
    // =======================================================
    private fun verificarConexion(txtEstado: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val response = api.getAllLeds()

                runOnUiThread {
                    if (response.isSuccessful) {
                        txtEstado.text = getString(R.string.estado_conectado)
                    } else {
                        txtEstado.text = getString(R.string.estado_error_conectar)
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    txtEstado.text = getString(R.string.estado_desconectado)
                }
            }
        }
    }
}
