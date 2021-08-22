package com.motive.degismedendegisin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.motive.degismedendegisin.fragment.PhoneCodeFragment
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.model.Users
import com.motive.degismedendegisin.utils.EventBusDataEvents
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(){
    lateinit var db  : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        db = Firebase.firestore

        setContentView(R.layout.activity_register)
        init()
    }

    private fun init(){
        etTelefon.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length >= 13){
                    devambutton.isEnabled = true
                    devambutton.setTextColor(ContextCompat.getColor(this@RegisterActivity,
                        R.color.aktif
                    ))
                }else{
                    devambutton.isEnabled = false
                    devambutton.setTextColor(ContextCompat.getColor(this@RegisterActivity,
                        R.color.inaktif
                    ))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length < 13){
                    devambutton.isEnabled = false
                    devambutton.setTextColor(ContextCompat.getColor(this@RegisterActivity,
                        R.color.inaktif
                    ))
                }

            }


        })

        back_to_registerlogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity,LoginRegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
            finish()
        }

        devambutton.setOnClickListener {
            registerprogress.visibility = View.VISIBLE
            var telNoKullanimda = false
            if (isValidPhone(etTelefon.text.toString())){
                var name = etİsim.text.toString()
                var surname = etSoyisim.text.toString()
                var telNo = etTelefon.text.toString()
                if (name.length >= 3 && surname.length >= 3){
                    db.collection("users").get().addOnSuccessListener {
                        for (user in it.toObjects(Users::class.java)){
                            if (telNo.equals(user.phone_number)){
                                registerprogress.visibility = View.GONE
                                Toast.makeText(this@RegisterActivity,"Telefon numarası kullanımda.",Toast.LENGTH_SHORT).show()
                                println("NUMARA KULLANIMDA")
                                telNoKullanimda = true
                                break
                            }
                        }
                        if (telNoKullanimda == false){
                            mainContainer.visibility = View.GONE
                            container_2.visibility = View.VISIBLE
                            registerprogress.visibility = View.GONE
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.container_2, PhoneCodeFragment())
                            transaction.addToBackStack("telefonKoduFragmentEklendi")
                            transaction.commit()
                            EventBus.getDefault().postSticky(EventBusDataEvents.KayitBilgileriniGonder(telNo,null,null, name,surname))
                        }
                    }.addOnFailureListener {
                        registerprogress.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity,"Bir hata meydana geldi. Lütfen internet bağlantınızı kontrol edin.",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    registerprogress.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity,"Lütfen tüm alanları eksiksiz doldurduğunuzdan emin olun.",Toast.LENGTH_LONG).show()
                }
            }else{
                registerprogress.visibility = View.GONE
                Toast.makeText(this@RegisterActivity,"Lütfen geçerli bir telefon numarası giriniz.",Toast.LENGTH_LONG).show()
            }

        }

        TVGirisyap.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            finish()
        }
    }

    override fun onBackPressed() {
        mainContainer.visibility = View.VISIBLE
        super.onBackPressed()
    }

    fun isValidPhone(phoneNumber : String) : Boolean{
        if (phoneNumber == null || phoneNumber.length > 13){
            return false
        }
        return android.util.Patterns.PHONE.matcher(phoneNumber).matches()
    }



}