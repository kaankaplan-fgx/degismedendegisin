package com.motive.degismedendegisin.model

class Randevular {
    var isim: String? = null
    var soyisim: String? = null
    var randevuTarihi: String? = null
    var operasyon: String? = null
    var telefonNo: String? = null
    var uuid: String? = null

    constructor()
    constructor(
        isim: String?,
        soyisim: String?,
        randevuTarihi: String?,
        operasyon: String?,
        telefonNo: String?,
        uuid: String?
    ) {
        this.isim = isim
        this.soyisim = soyisim
        this.randevuTarihi = randevuTarihi
        this.operasyon = operasyon
        this.telefonNo = telefonNo
        this.uuid = uuid
    }

    override fun toString(): String {
        return "Randevular(isim=$isim, soyisim=$soyisim, randevuTarihi=$randevuTarihi, operasyon=$operasyon, telefonNo=$telefonNo, user_id=$uuid)"
    }


}