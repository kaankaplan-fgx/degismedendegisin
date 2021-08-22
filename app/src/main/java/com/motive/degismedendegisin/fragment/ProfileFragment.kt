package com.motive.degismedendegisin.fragment
//DEVELOPED BY KAAN KAPLAN
//Unauthorized use, distribution and reproduction for any reason is prohibited.

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.model.Users
import com.motive.degismedendegisin.utils.EventBusDataEvents
import com.motive.degismedendegisin.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.header.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit


class ProfileFragment : Fragment() {
    lateinit var circleImage : CircleImageView
    lateinit var mAuth : FirebaseAuth
    lateinit var db  : FirebaseFirestore
    lateinit var kullaniciInfo : Users
    var profilePhotoUri:Uri? = null
    lateinit var mStorageRef : StorageReference
    val RESIM_SEC = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        circleImage = view.findViewById(R.id.CircleProfileFragment)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        mStorageRef = FirebaseStorage.getInstance().reference


        setupKullaniciBilgileri()


        TV_profile_photo_degistir.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent,RESIM_SEC)

        }

        tvsifredegis.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment, ChangePasswordFragment())
                commit()
                addToBackStack("sifreDegistir")
            }
        }

        guncelleButton.setOnClickListener {
            val user_id = mAuth.currentUser!!.uid
            var isim = et_profile_isim.text.toString()
            var soyisim = et_profile_soyisim.text.toString()
            var phoneNo = et_profile_tel.text.toString()

            var telNoKullanimda = false

            db.collection("users").get().addOnSuccessListener {
                if (kullaniciInfo.phone_number != phoneNo){
                    for (user in it.toObjects(Users::class.java)){
                        if (phoneNo == user!!.phone_number){
                            telNoKullanimda == true
                            break
                        }
                    }
                }
                if (telNoKullanimda == false){
                    if (kullaniciInfo.phone_number != phoneNo){
                        requireActivity().supportFragmentManager.beginTransaction().apply {
                            replace(R.id.fragment,EditPhoneCodeFragment()).addToBackStack("editFragAcıldı")
                            commit()
                            EventBus.getDefault().postSticky(EventBusDataEvents.EditBilgileriGonder(isim,soyisim,phoneNo))
                        }
                    }else if (kullaniciInfo.phone_number == phoneNo){
                        if (kullaniciInfo.name != isim || kullaniciInfo.surname != soyisim){
                            db.collection("users").document(user_id).update(mapOf(
                                "name" to isim,
                                "surname" to soyisim
                            ))
                            Toast.makeText(requireActivity(),"Bilgileriniz başarıyla güncellendi.",Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.beginTransaction().apply {
                                replace(R.id.fragment,InfoFragment())
                                commit()
                            }
                        }else if(profilePhotoUri == null && kullaniciInfo.phone_number == phoneNo && kullaniciInfo.surname == soyisim){
                            Toast.makeText(requireContext(),"Herhangi bir değişiklik yapmadınız.",Toast.LENGTH_SHORT).show()
                        }

                    }

                }else if (telNoKullanimda){
                    Toast.makeText(requireContext(),"Telefon numarası kullanımda.",Toast.LENGTH_SHORT).show()
                }
            }
            if (profilePhotoUri !=null){
                val loadingDialog = LoadingFragment()
                loadingDialog.show(requireActivity().supportFragmentManager,"loading")
                loadingDialog.isCancelable = false

                val user_uuid = mAuth.currentUser!!.uid
                mStorageRef.child("users").child(user_uuid).child("profile_picture").putFile(profilePhotoUri!!)
                    .addOnSuccessListener {
                        mStorageRef.child("users").child(user_uuid).child("profile_picture").downloadUrl.addOnSuccessListener {
                            db.collection("users").document(user_uuid).update(mapOf(
                                "profilePic" to it.toString()
                            ))
                            loadingDialog.dismiss()
                            Toast.makeText(requireActivity(),"Profil fotoğrafınız başarıyla güncellendi.",Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.beginTransaction().apply {
                                replace(R.id.fragment, InfoFragment())
                                commit()
                            }

                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),"Bir hata oluştu.",Toast.LENGTH_SHORT).show()
                            loadingDialog.dismiss()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),"Bir hata oluştu.",Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    }
            }
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESIM_SEC && resultCode == AppCompatActivity.RESULT_OK && data!!.data != null){
            profilePhotoUri = data!!.data
            CircleProfileFragment.setImageURI(profilePhotoUri)


        }
    }

    private fun setupKullaniciBilgileri() {
        et_profile_isim.setText(kullaniciInfo.name,TextView.BufferType.EDITABLE)
        et_profile_soyisim.setText(kullaniciInfo.surname,TextView.BufferType.EDITABLE)
        et_profile_tel.setText(kullaniciInfo.phone_number,TextView.BufferType.EDITABLE)
        var img = kullaniciInfo!!.profilePic!!
        UniversalImageLoader.setImage(img,CircleProfileFragment,progressbarProfileFragment,"")
    }



    @Subscribe(sticky = true)
    internal fun onKullaniciBilgileriEvent(kullaniciBilgileri : EventBusDataEvents.KullaniciBilgileriniGonder){
        kullaniciInfo = kullaniciBilgileri.kullanici!!
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