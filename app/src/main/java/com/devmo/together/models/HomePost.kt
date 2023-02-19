package com.devmo.together.models

import android.graphics.drawable.AnimatedImageDrawable

data class HomePost(
    var  id : String = "" ,
    var name :String = "" ,
    var userImg :String = "" ,
    var postBody : String = "" ,
    var postImage : String = "" ,
    val bankAccount : String = "" ,
    var location : String = ""
)
