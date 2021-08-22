package com.motive.adminapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.motive.adminapp.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var mAuth : FirebaseAuth
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()




        btngiris.setOnClickListener {
            val tel = etTel.text.toString()
            val sife = etPass.text.toString()
            val mail = tel+"@degismedendegisin.com"
            mAuth.signInWithEmailAndPassword(mail,sife).addOnCompleteListener {
                if (it.isSuccessful){
                    startActivity(Intent(this, AnasayfaActivity::class.java))
                    finish()
                }
            }
        }
        /*mAuth.createUserWithEmailAndPassword("+905380100116@degismedendegisin.com", "123456").addOnCompleteListener {
            if (it.isSuccessful){
                println("KAYDEDİLDİ")
            }
        }*/




    }
}