package com.example.dungziproject

data class User(
    var userId:String,
    var email:String,
    var name:String,
    var birth:String,
    var nickname:String,
    var image:String,
    var feeling:String
){
    constructor():this("","","","","","","")
}