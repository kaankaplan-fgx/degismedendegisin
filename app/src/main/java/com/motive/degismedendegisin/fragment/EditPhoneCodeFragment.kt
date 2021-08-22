package com.motive.degismedendegisin.fragment
//DEVELOPED BY KAAN KAPLAN
//Unauthorized use, distribution and reproduction for any reason is prohibited.

import android.content.Context
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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.model.Users
import com.motive.degismedendegisin.utils.EventBusDataEvents
import kotlinx.android.synthetic.main.fragment_edit_phone_code.*
import kotlinx.android.synthetic.main.fragment_edit_phone_code.view.*
import kotlinx.android.synthetic.main.fragment_phone_code.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit


class EditPhoneCodeFragment : Fragment() {
    lateinit var mAuth : FirebaseAuth
    lateinit var db  : FirebaseFirestore
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationID = ""
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    lateinit var gelentelNo : String
    var gelenkod = ""
    var name = ""
    var surname = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        setupCallback()


        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(gelentelNo)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)



        return inflater.inflate(R.layout.fragment_edit_phone_code, container, false)
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
                    Toast.makeText(requireActivity(),"Telefon numarası istenen formata uygun değil. Lütfen geçerli bir telefon numarası giriniz.",
                        Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(requireActivity(),"Çok fazla kod gönderildi, lütfen bir süre bekleyin ve tekrar deneyin.",
                        Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseNetworkException){
                    Toast.makeText(requireActivity(),"Lütfen internet bağlantınızı kontrol edin.",
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btn = view.btnGuncelleCode
        numaraCodeTV.text = gelentelNo
        etOnayKoduEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 6){
                    btnGuncelleCode.isEnabled = true
                    btnGuncelleCode.background = ContextCompat.getDrawable(requireContext(),R.drawable.profile_button)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length < 6 || s!!.length > 6){
                    btnGuncelleCode.isEnabled = false
                    btnGuncelleCode.background = ContextCompat.getDrawable(requireContext(),R.drawable.inaktif_button)
                }else{
                    btnGuncelleCode.isEnabled = true
                    btnGuncelleCode.background = ContextCompat.getDrawable(requireContext(),R.drawable.profile_button)
                }

            }

        })

        btn.setOnClickListener {
            println(etOnayKoduEdit.text.toString() + "///////////")
            println(gelenkod + "////////////////////")
            if (etOnayKoduEdit.text.toString() == gelenkod){
                var newEmail = gelentelNo + "@degismedendegisin.com"
                mAuth.currentUser!!.updateEmail(newEmail).addOnSuccessListener {
                    db.collection("users").document(mAuth.currentUser!!.uid).update(mapOf(
                        "name" to name,
                        "surname" to surname,
                        "phone_number" to gelentelNo,
                        "phone_mail" to newEmail
                    )).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(requireContext(),"Bilgiler başarıyla güncellendi.",Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.beginTransaction().apply {
                                replace(R.id.fragment,InfoFragment())
                                commit()
                            }
                        }else{
                            db.collection("users").document(mAuth.currentUser!!.uid).get().addOnSuccessListener {
                                val user = it.toObject(Users::class.java)
                                mAuth.currentUser!!.updateEmail(user!!.phone_mail!!).addOnCompleteListener {
                                    if (it.isSuccessful){
                                        Toast.makeText(requireContext(),"Telefon numaranız güncellenemedi. Lütfen daha sonra tekrar deneyin",Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(requireContext(),"Bir hata oluştu.",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                Toast.makeText(requireContext(),"Onay Kodu Hatalı",Toast.LENGTH_SHORT).show()
            }
        }

    }
    @Subscribe(sticky = true)
    internal fun onCodeEvent(editBilgileri : EventBusDataEvents.EditBilgileriGonder){
        gelentelNo = editBilgileri.telNo!!
        name = editBilgileri.yeniName!!
        surname = editBilgileri.surname!!

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

}