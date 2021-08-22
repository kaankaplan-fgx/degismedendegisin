package com.motive.degismedendegisin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.model.Users
import com.motive.degismedendegisin.utils.EventBusDataEvents
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.greenrobot.eventbus.EventBus


class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        progressBarForgot.visibility = View.GONE
        db = FirebaseFirestore.getInstance()


        etPhoneForgotPass.addTextChangedListener(watcher)

        backIV.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
            finish()
        }


        buttonPhoneDevam.setOnClickListener {
            progressBarForgot.visibility = View.VISIBLE
            val phoneNoET = etPhoneForgotPass.text.toString()
            db.collection("users").get().addOnSuccessListener {
                for (user in it.toObjects(Users::class.java)){
                    if (user.phone_number == phoneNoET){
                        var sifre = user.password
                        var telefon = user.phone_number
                        EventBus.getDefault().postSticky(EventBusDataEvents.SifreTelGonder(sifre,telefon))
                        println(sifre +" " + telefon)
                        var intent = Intent(this,ForgotPasswordCodeActivity::class.java)
                        intent.putExtra("phoneNo",phoneNoET)
                        progressBarForgot.visibility = View.GONE
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
                        finish()
                        break
                    }else{
                        progressBarForgot.visibility = View.GONE
                        Toast.makeText(this@ForgotPasswordActivity,"Kullanıcı bulunamadı.",Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                progressBarForgot.visibility = View.GONE
                Toast.makeText(this,"Bir şeyler yanlış gitti, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                Log.e("HATAForgot",it.message.toString())
            }

        }
    }



    var watcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (etPhoneForgotPass.text.toString().length == 13){
                buttonPhoneDevam.isEnabled = true
                buttonPhoneDevam.setTextColor(ContextCompat.getColor(this@ForgotPasswordActivity,R.color.aktif))
            }else{
                buttonPhoneDevam.isEnabled = false
                buttonPhoneDevam.setTextColor(ContextCompat.getColor(this@ForgotPasswordActivity,R.color.inaktif))
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }


}