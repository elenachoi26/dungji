package com.example.dungziproject.navigation.model

data class ContentDTO (var explain: String? = null,
                       var imgUrl : String? = null,
                       var userId : String? = null,
                       var nickname : String? = null,
                       var timestamp : Long? = null,
                       var favoriteCount : Int = 0,
                       var commentCount : Int = 0,
                       var favorites : MutableMap<String,Boolean> = HashMap()) {

    data class Comment(var userId: String? = null,
                       var nickname : String? = null,
                       var comment : String? = null,
                       var commentCount: Int = 0,
                       var timestamp : Long? = null)
}