package com.motive.degismedendegisin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.motive.degismedendegisin.R
import kotlinx.android.synthetic.main.activity_login_register.*

class LoginRegisterActivity : AppCompatActivity() {
    lateinit var mAuthListener : FirebaseAuth.AuthStateListener
    lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login_register)
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()


        btnKayitOl.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            finish()
        }
        btnGirisYap.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            finish()
        }
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    startActivity(Intent(this@LoginRegisterActivity,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    finish()
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}