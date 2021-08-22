package com.motive.adminapp.model

class Users {
    var phone_number: String? = null
    var password: String? = null
    var name: String? = null
    var surname: String? = null
    var phone_mail: String? = null
    var uuid: String? = null
    var profilePic: String? = null


    constructor()
    constructor(
        phone_number: String?,
        password: String?,
        name: String?,
        surname: String?,
        phone_mail: String?,
        uuid: String?,
        profilePic: String?
    ) {
        this.phone_number = phone_number
        this.password = password
        this.name = name
        this.surname = surname
        this.phone_mail = phone_mail
        this.uuid = uuid
        this.profilePic = profilePic
    }

    override fun toString(): String {
        return "Users(phone_number=$phone_number, password=$password, name=$name, surname=$surname, phone_mail=$phone_mail, uuid=$uuid, profilePic=$profilePic)"
    }

}