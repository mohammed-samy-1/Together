package com.devmo.together.models

data class User(
    val id:String = "",
    val name:String = "",
    val email:String = "",
    var imageURL:String = "",
    val passL:Int = 0 ,
)
