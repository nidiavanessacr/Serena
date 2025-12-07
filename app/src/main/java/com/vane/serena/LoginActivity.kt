package com.vane.serena

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vane.serena.network.LoginBody
import com.vane.serena.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ============================
        // SI YA HAY SESIÓN → IR A MAIN
        // ============================
        val prefs = getSharedPreferences("serena_prefs", Context.MODE_PRIVATE)
        val savedUser = prefs.getInt("user_id", -1)

        if (savedUser != -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val inputUser = findViewById<EditText>(R.id.inputUser)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoRegister = findViewById<Button>(R.id.btnGoRegister)

        // ========== BOTÓN LOGIN ==========
        btnLogin.setOnClickListener {
            val username = inputUser.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            iniciarSesion(username, password)
        }

        // ========== IR A REGISTER ==========
        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


    // ================================================================
    //     FUNCIÓN DE LOGIN QUE SE COMUNICA CON LA API
    // ================================================================
    private fun iniciarSesion(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val body = LoginBody(username, password)
                val response = api.loginUser(body)

                runOnUiThread {

                    if (response.isSuccessful) {

                        val userId = response.body()?.user_id ?: -1

                        if (userId != -1) {

                            // ============================
                            // GUARDAR SESIÓN COMPLETA
                            // ============================
                            val prefs = getSharedPreferences("serena_prefs", Context.MODE_PRIVATE)
                            prefs.edit()
                                .putInt("user_id", userId)
                                .putString("username", username)  // ⭐ Guardamos el nombre
                                .apply()

                            Toast.makeText(
                                this@LoginActivity,
                                "Bienvenido $username",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Ir al MainActivity
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Credenciales incorrectas",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error al iniciar sesión",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
