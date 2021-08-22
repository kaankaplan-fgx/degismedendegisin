package com.motive.degismedendegisin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.motive.degismedendegisin.R

class SplashScreen : AppCompatActivity() {
    private val SPLASH_TIME : Long = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            startActivity(Intent(this, LoginRegisterActivity::class.java))
            overridePendingTransition(R.anim.zoom_in,R.anim.static_animation)
            finish()
        }, SPLASH_TIME)
    }
}