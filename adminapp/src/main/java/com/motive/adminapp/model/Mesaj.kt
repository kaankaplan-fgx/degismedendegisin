package com.motive.adminapp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Mesaj {

    var isim:String? = null
    var soyisim:String? = null
    var telefon:String? = null
    var mesaj:String? = null
    var goruldu:Boolean? = null
    var type:String? = null
    var mesajGonderenID:String? = null

    @ServerTimestamp
    var time: Date? = null

    constructor()
    constructor(
        isim: String?,
        soyisim: String?,
        telefon: String?,
        mesaj: String?,
        goruldu: Boolean?,
        time: Date?,
        type: String?,
        mesajGonderenID: String?
    ) {
        this.isim = isim
        this.soyisim = soyisim
        this.telefon = telefon
        this.mesaj = mesaj
        this.goruldu = goruldu
        this.time = time
        this.type = type
        this.mesajGonderenID = mesajGonderenID
    }

    override fun toString(): String {
        return "Mesaj(isim=$isim, soyisim=$soyisim, telefon=$telefon, mesaj=$mesaj, goruldu=$goruldu, time=$time, type=$type, user_id=$mesajGonderenID)"
    }

}