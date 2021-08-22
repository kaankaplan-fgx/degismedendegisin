package com.motive.degismedendegisin.model

class Users {
    var phone_number: String? = null
    var password: String? = null
    var name: String? = null
    var surname: String? = null
    var phone_mail: String? = null
    var uuid: String? = null
    var profilePic: String? = null
    var fcm_token:String? = null

    constructor()
    constructor(
        phone_number: String?,
        password: String?,
        name: String?,
        surname: String?,
        phone_mail: String?,
        uuid: String?,
        profilePic: String?,
        fcm_token: String?
    ) {
        this.phone_number = phone_number
        this.password = password
        this.name = name
        this.surname = surname
        this.phone_mail = phone_mail
        this.uuid = uuid
        this.profilePic = profilePic
        this.fcm_token = fcm_token
    }

    override fun toString(): String {
        return "Users(phone_number=$phone_number, password=$password, name=$name, surname=$surname, phone_mail=$phone_mail, uuid=$uuid, profilePic=$profilePic, fcm_token=$fcm_token)"
    }


}