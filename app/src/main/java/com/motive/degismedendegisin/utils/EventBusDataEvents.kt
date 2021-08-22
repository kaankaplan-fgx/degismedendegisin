package com.motive.degismedendegisin.utils

import com.motive.degismedendegisin.model.Users

class EventBusDataEvents {
    internal class KayitBilgileriniGonder(var telNo: String?, var verificationID:String?,var code : String?,var isim : String?,var soyisim : String?)
    internal class KullaniciBilgileriniGonder(var kullanici:Users?)
    internal class EditBilgileriGonder(var yeniName:String?,var surname:String?,var telNo:String?)
    internal class SifreTelGonder(var phone:String?,var sifre:String?)
}