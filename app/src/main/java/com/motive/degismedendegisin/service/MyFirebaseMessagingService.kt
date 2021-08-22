package com.motive.degismedendegisin.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.helpcrunch.library.core.HelpCrunch
import com.helpcrunch.library.core.isHelpCrunchMessage
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.activity.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(p0: RemoteMessage) {
        createChannel()

        if (p0.isHelpCrunchMessage().not()) {
            var bildirimBaslik = p0!!.notification!!.title
            var bildirimBody = p0!!.notification!!.body
            //var bildirimData = p0!!.data
            Log.e("FCM","BAŞLIK : ${bildirimBaslik} GOVDE : ${bildirimBody}")
            bildirimGoster(bildirimBaslik,bildirimBody)
        } else {
            HelpCrunch.showNotification(p0)
        }



    }

    private fun bildirimGoster(bildirimBaslik: String?, bildirimBody: String?) {
        var pendingIntent = Intent(this,MainActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("bildirimTitle",bildirimBaslik)
        pendingIntent.putExtra("bildirimBody",bildirimBody)

        var bildirimPendingIntenti = PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(this,"RandevuIstegi")
            .setSmallIcon(R.drawable.ic_icon_bildirim)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_icon_bildirim))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(bildirimBaslik)
            .setContentText(bildirimBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(bildirimPendingIntenti)
            .build()
        var noticicationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        noticicationManager.notify(System.currentTimeMillis().toInt(),builder)

    }

    private fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "NotificationTitle"
            val descriptionText = "NotificationDesc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("RandevuIstegi",name,importance).apply {
                description = descriptionText

            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(p0: String) {
        var yeniToken = p0!!
        yeniTokenVeritabanınaKaydet(yeniToken)
    }

    private fun yeniTokenVeritabanınaKaydet(yeniToken : String) {
        if (FirebaseAuth.getInstance().currentUser != null){
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).update("fcm_token",yeniToken)

        }
    }
}