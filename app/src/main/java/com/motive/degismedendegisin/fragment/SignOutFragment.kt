package com.motive.degismedendegisin.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.motive.degismedendegisin.activity.LoginRegisterActivity


class SignOutFragment : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var alert = AlertDialog.Builder(requireActivity())
            .setTitle("Çıkış")
            .setMessage("Çıkış yapmak istediğinizden emin misiniz?")
            .setPositiveButton("Çıkış Yap",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(requireActivity(),LoginRegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                    requireActivity().finish()
                }

            })
            .setNegativeButton("İptal",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dismiss()
                }

            }).create()
        return alert
    }




}