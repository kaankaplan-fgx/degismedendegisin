package com.motive.degismedendegisin.fragment

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.model.Users
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_password.*


class ChangePasswordFragment : Fragment() {
    lateinit var mAuth : FirebaseAuth
    lateinit var db  : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        val user_id = mAuth.currentUser!!.uid

        etNewPass_bir.addTextChangedListener(watcher)
        etNewPass_iki.addTextChangedListener(watcher)

        buttonsifredegis.setOnClickListener {
            changePassProgress.visibility = View.VISIBLE
            if (etNewPass_iki.text.toString() == etNewPass_bir.text.toString()){
                db.collection("users").document(user_id).get().addOnSuccessListener {
                    val user = it.toObject(Users::class.java)
                    if (etOldPass.text.toString() == user!!.password){
                        println(user!!.password + "/////////////")
                        println(etOldPass.text.toString() + "////////////")
                        mAuth.currentUser!!.updatePassword(etNewPass_bir.text.toString()).addOnSuccessListener {
                            db.collection("users").document(user_id).update(mapOf(
                                "password" to etNewPass_iki.text.toString()
                            )).addOnSuccessListener {
                                Toast.makeText(requireContext(),"Şifreniz başarıyla değiştirildi.",Toast.LENGTH_SHORT).show()
                                changePassProgress.visibility = View.GONE
                                requireActivity().supportFragmentManager.beginTransaction().apply {
                                    replace(R.id.fragment, InfoFragment())
                                    commit()
                                }
                            }
                        }.addOnFailureListener {
                            changePassProgress.visibility = View.GONE
                            Log.e("hata", it.message.toString())
                            Toast.makeText(requireContext(),"Bu işlem hassastır ve yakın zamanda kimlik doğrulaması gerektirir. Bu isteği yeniden denemeden önce tekrar oturum açın.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        changePassProgress.visibility = View.GONE
                        Toast.makeText(requireContext(),"Eski şifreniz yanlış girildi. Lütfen tekrar girin.",Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    changePassProgress.visibility = View.GONE
                    Log.e("hata", it.message.toString())
                    Toast.makeText(requireContext(),"Bir hata oluştu.",Toast.LENGTH_SHORT).show()
                }
            }else{
                changePassProgress.visibility = View.GONE
                Toast.makeText(requireContext(),"Girdiğiniz şifreler aynı değil.",Toast.LENGTH_SHORT).show()
            }
        }


    }

    var watcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length > 6 ){
                if (etNewPass_bir.text.toString().length > 6 && etNewPass_iki.text.toString().length > 6){
                    buttonsifredegis.isEnabled = true
                    buttonsifredegis.background = (ContextCompat.getDrawable(requireActivity(),R.drawable.profile_button))
                }else{
                    buttonsifredegis.isEnabled = false
                    buttonsifredegis.background = (ContextCompat.getDrawable(requireActivity(),R.drawable.inaktif_button))
                }
            }else{
                buttonsifredegis.isEnabled = false
                buttonsifredegis.background = (ContextCompat.getDrawable(requireActivity(),R.drawable.inaktif_button))
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (s!!.length <= 6){
                buttonsifredegis.isEnabled = false
                buttonsifredegis.background = (ContextCompat.getDrawable(requireActivity(),R.drawable.inaktif_button))
            }else{
                if (etNewPass_bir.text.toString().length > 6 && etNewPass_iki.text.toString().length > 6){
                    buttonsifredegis.isEnabled = true
                    buttonsifredegis.background = (ContextCompat.getDrawable(requireActivity(),R.drawable.profile_button))
                }
            }

        }

    }

}