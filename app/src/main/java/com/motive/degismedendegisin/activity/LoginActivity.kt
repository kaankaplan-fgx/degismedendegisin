package com.motive.degismedendegisin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.motive.degismedendegisin.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var mAuth : FirebaseAuth
    private var mIsShowPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        tvkayitol.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            finish()
        }

        back_login_to_loginregister.setOnClickListener {
            startActivity(Intent(this@LoginActivity,LoginRegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
            finish()
        }

        etLoginPhone.addTextChangedListener(watcher)
        etLoginPassword.addTextChangedListener(watcher)



        btnLogin.setOnClickListener {
            loginprogress.visibility = View.VISIBLE
            var phone = etLoginPhone.text.toString()
            var password = etLoginPassword.text.toString()
            var phonemail = phone+"@degismedendegisin.com"
            mAuth.signInWithEmailAndPassword(phonemail,password).addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful){
                        fcmTokenKaydet()
                        loginprogress.visibility = View.GONE
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                        finish()
                    }else{
                        loginprogress.visibility = View.GONE
                        Toast.makeText(this@LoginActivity,"Telefon numarası veya şifre hatalı.",Toast.LENGTH_SHORT).show()
                        Log.e("HATA","HATA : " + p0.exception)
                    }
                }

            })

        }
        showpass.setOnClickListener {
            mIsShowPass = !mIsShowPass
            showPass(mIsShowPass)
        }
        showPass(mIsShowPass)

        forgotpassword.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            finish()
        }
    }


    private fun fcmTokenKaydet(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            yeniTokenVeritabanınaKaydet(token)

        })
    }

    private fun yeniTokenVeritabanınaKaydet(yeniToken : String) {
        if (FirebaseAuth.getInstance().currentUser != null){
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).update("fcm_token",yeniToken).addOnSuccessListener {

            }.addOnFailureListener {
                Log.e("FCM",it.message.toString())
            }

        }
    }


    var watcher : TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (etLoginPhone.text.toString().length >= 10 && etLoginPassword.text.toString().length >= 6 ){
                btnLogin.isEnabled = true
                btnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.aktif))
            }else{
                btnLogin.isEnabled = false
                btnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.inaktif))
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@LoginActivity,LoginRegisterActivity::class.java))
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
        finish()
        super.onBackPressed()
    }
    private fun showPass(isShow : Boolean){
        if (isShow){
            etLoginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            showpass.setImageResource(R.drawable.ic_notshow_pass)
        }else{
            etLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            showpass.setImageResource(R.drawable.ic_show_pass)
        }
        etLoginPassword.setSelection(etLoginPassword.text.toString().length)
    }



}