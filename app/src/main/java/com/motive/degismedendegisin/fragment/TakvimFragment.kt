package com.motive.degismedendegisin.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.model.Randevular
import com.motive.degismedendegisin.model.Users
import com.motive.degismedendegisin.service.MyFirebaseMessagingService
import com.motive.degismedendegisin.utils.EventBusDataEvents
import kotlinx.android.synthetic.main.fragment_takvim.*
import kotlinx.android.synthetic.main.succes_dialog.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.joda.time.DateTime


class TakvimFragment : Fragment(),DatePickerListener {
    var tarihEskiMi = false
    lateinit var db : FirebaseFirestore
    var date = ""
    var isim = ""
    var soyisim = ""
    var phone = ""
    var operasyon = ""
    var user_id = ""
    lateinit var succesBtn : Button
    lateinit var kullaniciInfo : Users
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_takvim, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val picker = datePicker
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.succes_dialog)
        dialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.backround_dialog))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.animation

        succesBtn = dialog.findViewById(R.id.successokbtn)

        picker.setListener(this).showTodayButton(false).init()

        picker.setDate(DateTime().plusDays(1))

        succesBtn.setOnClickListener {
            dialog.dismiss()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment,InfoFragment())
                commit()
            }
        }

        btnRandevuAl.setOnClickListener {
            var operasyon = etOperasyon.text.toString()
            if (operasyon.trim().length <= 0){
                Toast.makeText(requireContext(),"Operasyon kısmı boş bırakılamaz",Toast.LENGTH_SHORT).show()
            }else{
                if (tarihEskiMi == true){
                    Toast.makeText(requireContext(),"Geçmiş tarihli randevu alamazsınız!",Toast.LENGTH_SHORT).show()
                }else{
                    randevuAlProgress.visibility = View.VISIBLE
                    isim = kullaniciInfo.name!!
                    soyisim = kullaniciInfo.surname!!
                    phone = kullaniciInfo.phone_number!!
                    user_id = kullaniciInfo.uuid!!
                    var operasyon = etOperasyon.text.toString()
                    var randevu = Randevular(isim,soyisim,date,operasyon,phone,user_id)

                    db.collection("randevular").document(user_id).set(randevu).addOnSuccessListener {
                        randevuAlProgress.visibility = View.GONE
                        dialog.show()
                    }.addOnFailureListener {
                        randevuAlProgress.visibility = View.GONE
                        Toast.makeText(requireContext(),"Bir şeyler yanlış gitti, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                        Log.e("HATA",it.message.toString())
                    }.addOnCanceledListener {
                        randevuAlProgress.visibility = View.GONE
                        Toast.makeText(requireContext(),"Bir şeyler yanlış gitti, lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    override fun onDateSelected(dateSelected: DateTime?) {
        if (dateSelected!!.compareTo(DateTime()) < DateTime().compareTo(DateTime())){
            tarihEskiMi = true
        }else{
            tarihEskiMi = false
            date = dateSelected.toString()
        }
    }

    @Subscribe(sticky = true)
    internal fun onRandevuEvent(kullaniciBilgileri : EventBusDataEvents.KullaniciBilgileriniGonder){
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