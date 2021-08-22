package com.motive.degismedendegisin.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.activity.AmeliyatActivity
import com.motive.degismedendegisin.activity.KvkkActivity
import com.motive.degismedendegisin.activity.LoginRegisterActivity
import com.motive.degismedendegisin.activity.MainActivity
import com.motive.degismedendegisin.model.Users
import com.motive.degismedendegisin.utils.EventBusDataEvents
import kotlinx.android.synthetic.main.fragment_password.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception


class PasswordFragment : Fragment() {
    var telNo = ""
    var verificationID = ""
    var gelenKod = ""
    var isim = ""
    var soyisim = ""
    lateinit var mAuth : FirebaseAuth
    lateinit var db  : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        etsifrebir.addTextChangedListener(watcher)
        etsifreiki.addTextChangedListener(watcher)

        textView4.setOnClickListener {
            startActivity(Intent(requireContext(),KvkkActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            //addtobackpress eklenicek
        }

        button2.setOnClickListener {
            passwordProgress.visibility = View.VISIBLE
            if (etsifrebir.text.toString().equals(etsifreiki.text.toString())){
                val password = etsifrebir.text.toString()
                val name = isim
                val soyismi = soyisim
                val telNo = telNo
                val phone_mail = telNo+"@degismedendegisin.com"

                mAuth.createUserWithEmailAndPassword(phone_mail,password).addOnCompleteListener(object  : OnCompleteListener<AuthResult>{
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful){
                            var user_id = mAuth.currentUser!!.uid
                            var kaydedilecekkullanici= Users(telNo,password,name,soyismi,phone_mail,user_id,"","")
                            db.collection("users").document(user_id).set(kaydedilecekkullanici).addOnSuccessListener {
                                fcmTokenKaydet()
                                passwordProgress.visibility = View.GONE
                                startActivity(Intent(requireActivity(),AmeliyatActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                                requireActivity().finish()
                            }.addOnFailureListener {
                                Log.e("HATA", it.message.toString())
                                mAuth.currentUser!!.delete().addOnCompleteListener(object : OnCompleteListener<Void>{
                                    override fun onComplete(p0: Task<Void>) {
                                        if (p0.isSuccessful){
                                            passwordProgress.visibility = View.GONE
                                            Toast.makeText(requireActivity(),"Kaydolma başarısız. Lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(requireActivity(),LoginRegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                                            requireActivity().finish()
                                        }
                                    }

                                })

                            }
                        }else{
                            passwordProgress.visibility = View.GONE
                            Toast.makeText(requireActivity(),"Kaydolma başarısız. Lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                        }
                    }

                })


            }else{
                passwordProgress.visibility = View.GONE
                Toast.makeText(requireActivity(),"Şifreler eşleşmiyor",Toast.LENGTH_SHORT).show()
            }
        }

    }

    var watcher : TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length > 6 ){
                if (etsifrebir.text.toString().length > 6 && etsifreiki.text.toString().length > 6){
                    button2.isEnabled = true
                    button2.setTextColor(ContextCompat.getColor(requireActivity(),R.color.aktif))
                }else{
                    button2.isEnabled = false
                    button2.setTextColor(ContextCompat.getColor(requireActivity(),R.color.inaktif))
                }
            }else{
                button2.isEnabled = false
                button2.setTextColor(ContextCompat.getColor(requireActivity(),R.color.aktif))
            }
        }
        override fun afterTextChanged(s: Editable?) {
            if (s!!.length <= 6){
                button2.isEnabled = false
                button2.setTextColor(ContextCompat.getColor(requireActivity(),R.color.inaktif))
            }else{
                if (etsifrebir.text.toString().length > 6 && etsifreiki.text.toString().length > 6){
                    button2.isEnabled = true
                    button2.setTextColor(ContextCompat.getColor(requireActivity(),R.color.aktif))
                }
            }
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


    ///////////////////////EVENTBUS/////////////////////////////
    @Subscribe(sticky = true)
    internal fun onPasswordEvent(kayitBilgileri : EventBusDataEvents.KayitBilgileriniGonder){
        telNo = kayitBilgileri.telNo!!
        gelenKod = kayitBilgileri.code!!
        isim = kayitBilgileri.isim!!
        soyisim = kayitBilgileri.soyisim!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
    ///////////////////////EVENTBUS/////////////////////////////

}