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
import androidx.core.widget.doOnTextChanged
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.activity.LoginActivity
import com.motive.degismedendegisin.utils.EventBusDataEvents
import kotlinx.android.synthetic.main.fragment_phone_code.*
import kotlinx.android.synthetic.main.fragment_phone_code.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


class PhoneCodeFragment : Fragment() {

    var gelentelNo = ""
    var isim = ""
    var soyisim = ""
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationID = ""
    var gelenkod = ""
    lateinit var auth : FirebaseAuth
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_phone_code, container, false)


        setupCallback()



        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(gelentelNo)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        return view
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
                    Toast.makeText(requireActivity(),"Telefon numarası istenen formata uygun değil. Lütfen geçerli bir telefon numarası giriniz.",Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(requireActivity(),"Çok fazla kod gönderildi, lütfen bir süre bekleyin ve tekrar deneyin.",Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseNetworkException){
                    Toast.makeText(requireActivity(),"Lütfen internet bağlantınızı kontrol edin.",Toast.LENGTH_SHORT).show()
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
        btntelkodeileri.setOnClickListener {
            println("ONAY KODU : ${gelenkod.toString()}")
            println("ONAY KODU : ${gelenkod.toString()}")
            println("ONAY KODU : ${gelenkod.toString()}")
            Log.e("tag", gelenkod)
            if (gelenkod.equals(etOnayKodu.text.toString())){
                EventBus.getDefault().postSticky(EventBusDataEvents.KayitBilgileriniGonder(gelentelNo,verificationID,gelenkod,isim,soyisim))
                var transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_2, PasswordFragment())
                transaction.addToBackStack("passwordFragmentEklendi")
                transaction.commit()

            }else{
                Toast.makeText(activity,"Onay Kodu Hatalı",Toast.LENGTH_SHORT).show()
            }
        }

        etOnayKodu.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 6){
                    btntelkodeileri.isEnabled = true
                    btntelkodeileri.setTextColor(ContextCompat.getColor(activity!!,R.color.aktif))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length < 6 || s!!.length > 6){
                    btntelkodeileri.isEnabled = false
                    btntelkodeileri.setTextColor(ContextCompat.getColor(activity!!,R.color.inaktif))
                }else{
                    btntelkodeileri.isEnabled = true
                    btntelkodeileri.setTextColor(ContextCompat.getColor(activity!!,R.color.aktif))
                }

            }

        })

        tvgirisyaponaykodu.setOnClickListener {
            startActivity(Intent(requireActivity(),LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            requireActivity().finish()
        }


    }

    @Subscribe (sticky = true)
    internal fun onTelefonNoEvent(kayitBilgileri : EventBusDataEvents.KayitBilgileriniGonder){
        gelentelNo = kayitBilgileri.telNo!!
        isim = kayitBilgileri.isim!!
        soyisim = kayitBilgileri.soyisim!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this@PhoneCodeFragment)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this@PhoneCodeFragment)
    }




}