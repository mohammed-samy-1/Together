package com.devmo.together.models

data class DemandPost(
    var id: String = "",
    var name: String = "",
    var userImg: String = "",
    val postBody: String = "",
    var postImage: String = "",
    val phone: String = "",
    val location: String = "",
)
