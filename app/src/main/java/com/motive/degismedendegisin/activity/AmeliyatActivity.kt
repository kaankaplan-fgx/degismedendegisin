package com.motive.degismedendegisin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.motive.degismedendegisin.R
import kotlinx.android.synthetic.main.activity_ameliyat.*

class AmeliyatActivity : AppCompatActivity() {
    lateinit var mAuth : FirebaseAuth
    lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.statusBarColor = ContextCompat.getColor(this@AmeliyatActivity,R.color.splashbackround)
        setContentView(R.layout.activity_ameliyat)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()



        ayaktamavi.setOnClickListener {
            ayaktamavi.setImageResource(R.drawable.ayaktanormal)
            ameliyatmavi.setImageResource(R.drawable.kesikameliyat)
        }

        ameliyatmavi.setOnClickListener {
            ayaktamavi.setImageResource(R.drawable.kesik)
            ameliyatmavi.setImageResource(R.drawable.ameilyatnormal)
        }

        btnİstiyorum.setOnClickListener {
            val kullanici = hashMapOf<String,String>(
                "user_id" to mAuth.currentUser!!.uid,
                "ameliyatOlmusMu" to "Hayır"
            )


            db.collection("ameliyatistegi").document(mAuth.currentUser!!.uid).set(kullanici).addOnCompleteListener {
                if (it.isSuccessful){
                    val intent = Intent(this@AmeliyatActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,"Bir şeyler yanlış gitti, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                    Log.e("AmeliyatError",it.exception.toString())
                }
            }

        }

        btnAmeliyatOldum.setOnClickListener {
            val kullanici = hashMapOf<String,String>(
                "user_id" to mAuth.currentUser!!.uid,
                "ameliyatOlmusMu" to "Evet"
            )
            db.collection("ameliyatistegi").document(mAuth.currentUser!!.uid).set(kullanici).addOnCompleteListener {
                if (it.isSuccessful){
                    val intent = Intent(this@AmeliyatActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,"Bir şeyler yanlış gitti, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                    Log.e("AmeliyatError",it.exception.toString())
                }

            }


        }
    }
}