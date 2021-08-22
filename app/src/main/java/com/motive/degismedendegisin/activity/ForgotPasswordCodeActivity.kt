package com.motive.degismedendegisin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.motive.degismedendegisin.R
import kotlinx.android.synthetic.main.activity_forgot_password_code.*
import java.util.concurrent.TimeUnit

class ForgotPasswordCodeActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    var phoneNoo = ""
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var gelenkod = ""
    var verificationID = ""
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_code)

        phoneNoo = intent.getStringExtra("phoneNo")!!
        auth = FirebaseAuth.getInstance()

        tvPhoneNoForgot.text = phoneNoo



        setupCallback()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNoo)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        ivBackOnayKodu.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
            finish()
        }

        btnOnayKoduForgot.setOnClickListener {
            if (gelenkod == etOnayKoduForgot.text.toString()){
                var intent = Intent(this,ResetPasswordActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)

                finish()
            }else{
                Toast.makeText(this,"Doğrulama kodu hatalı.",Toast.LENGTH_SHORT).show()
            }
        }

        etOnayKoduForgot.addTextChangedListener(watcher)
    }

    private fun setupCallback() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (credential.smsCode.isNullOrEmpty()){
                    gelenkod = credential.smsCode!!
                    println("ON VERİFİ ÇALIŞTI")
                }
                gelenkod = credential.smsCode!!

            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.e("TAG", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this@ForgotPasswordCodeActivity,"Telefon numarası istenen formata uygun değil. Lütfen geçerli bir telefon numarası giriniz.",
                        Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(this@ForgotPasswordCodeActivity,"Çok fazla kod gönderildi, lütfen bir süre bekleyin ve tekrar deneyin.",
                        Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseNetworkException){
                    Toast.makeText(this@ForgotPasswordCodeActivity,"Lütfen internet bağlantınızı kontrol edin.",
                        Toast.LENGTH_SHORT).show()
                }


            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                println("ON CODE SEND ÇALIŞTI")
                verificationID = verificationId
                resendToken = token
            }
        }

    }

    var watcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length == 6){
                btnOnayKoduForgot.isEnabled == true
                btnOnayKoduForgot.setTextColor(ContextCompat.getColor(this@ForgotPasswordCodeActivity,R.color.aktif))
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (s!!.length < 6 || s!!.length > 6){
                btnOnayKoduForgot.isEnabled = false
                btnOnayKoduForgot.setTextColor(ContextCompat.getColor(this@ForgotPasswordCodeActivity,R.color.inaktif))
            }else{
                btnOnayKoduForgot.isEnabled = true
                btnOnayKoduForgot.setTextColor(ContextCompat.getColor(this@ForgotPasswordCodeActivity,R.color.aktif))
            }

        }


    }
}