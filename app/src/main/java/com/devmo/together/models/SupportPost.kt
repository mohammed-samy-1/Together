package com.devmo.together.models

data class SupportPost(
    var id: String = "",
    var name: String = "",
    var userImg: String = "",
    val postBody: String = "",
    var postImage: String = "",
    val phone: String = "",
    val location: String = "",
    val gender: String = "",
    val rooms: Int = 0,
    val people: Int = 0,
    var isRequest: Boolean = false
)
