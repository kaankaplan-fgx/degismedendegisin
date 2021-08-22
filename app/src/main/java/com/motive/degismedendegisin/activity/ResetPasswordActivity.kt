package com.motive.degismedendegisin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.utils.EventBusDataEvents
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var db : FirebaseFirestore
    lateinit var mAuth : FirebaseAuth
    var phone = ""
    var sifre = ""
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()



        btnResetPassUpdate.setOnClickListener {
            resetprogress.visibility = View.VISIBLE
            var sifrebir = etResPasbir.text.toString()
            var sifreiki = etResPasbir.text.toString()
            if (sifrebir.equals(sifreiki)){
                mAuth.signInWithEmailAndPassword(email,phone).addOnSuccessListener {
                    mAuth.currentUser!!.updatePassword(sifrebir).addOnSuccessListener {
                        db.collection("users").document(mAuth.currentUser!!.uid).update("password",sifrebir).addOnSuccessListener {
                            resetprogress.visibility = View.GONE
                            Toast.makeText(this,"Şifreniz başarıyla güncellendi..",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                            finish()
                        }.addOnFailureListener {
                            resetprogress.visibility = View.GONE
                            Toast.makeText(this,"Bir şeyler yanlış gitti, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                            Log.e("hata",it.message.toString())
                        }
                    }.addOnFailureListener {
                        resetprogress.visibility = View.GONE
                        Toast.makeText(this,"Bir şeyler yanlış gitti, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                        Log.e("hata",it.message.toString())
                    }
                }.addOnFailureListener {
                    resetprogress.visibility = View.GONE
                    Toast.makeText(this,"Bir şeyler yanlış gitti, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                    Log.e("hata",it.message.toString())
                }
            }else{
                resetprogress.visibility = View.GONE
                Toast.makeText(this,"Girdiğiniz şifreler eşleşmiyor..",Toast.LENGTH_SHORT).show()
            }
        }



    }
    @Subscribe(sticky = true)
    internal fun onTelSifreAl(telsifre : EventBusDataEvents.SifreTelGonder){
        phone = telsifre.phone!!
        sifre = telsifre.sifre!!
        email = sifre+"@degismedendegisin.com"
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
