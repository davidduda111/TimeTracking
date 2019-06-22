package com.example.semestralka_vamz.models

class User {
    companion object Factory {
        fun create(): User = User()
    }
    var objectId: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var salary: String? = null
    var currency: String? = null
    var time: ArrayList<Any>? = null
}