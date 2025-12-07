package com.vane.serena

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vane.serena.network.RegisterBody
import com.vane.serena.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val inputUser = findViewById<EditText>(R.id.inputUserRegister)
        val inputPassword = findViewById<EditText>(R.id.inputPasswordRegister)
        val inputPassword2 = findViewById<EditText>(R.id.inputPasswordRegister2)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoLogin = findViewById<Button>(R.id.btnGoLogin)

        // =============== BOTÓN REGISTRAR ===============
        btnRegister.setOnClickListener {
            val username = inputUser.text.toString().trim()
            val pass1 = inputPassword.text.toString().trim()
            val pass2 = inputPassword2.text.toString().trim()

            if (username.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_fill_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                Toast.makeText(this, getString(R.string.msg_password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(username, pass1)
        }

        // =============== VOLVER A LOGIN ===============
        btnGoLogin.setOnClickListener {
            finish() // vuelve a Login
        }
    }

    // ============================================================
    // FUNCIÓN PARA REGISTRAR EN LA API
    // ============================================================
    private fun registrarUsuario(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val api = RetrofitClient.instance
                val body = RegisterBody(username, password)

                val response = api.registerUser(body)

                runOnUiThread {

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.msg_register_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        // Ir a Login
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()

                    } else if (response.code() == 409) {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.msg_user_exists),
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.msg_register_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
