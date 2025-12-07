package com.vane.serena

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        DynamicColors.applyToActivitiesIfAvailable(application)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        val logo = findViewById<ImageView>(R.id.logoSerena)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarSplash)

        // Animación del logo
        logo.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.fade_in)
        )

        // Llenado de la barra
        GlobalScope.launch(Dispatchers.Main) {

            for (i in 0..100 step 5) {
                progressBar.progress = i
                delay(80)
            }

            // pequeña animación de salida
            logo.startAnimation(AnimationUtils.loadAnimation(this@SplashActivity, R.anim.fade_out))
            delay(400)

            // ir al login
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            finish()
        }
    }
}
