package com.example.dungziproject


data class Message(
    var message: String?,
    var sendId: String?,
    var sendTime: String?,
    var senderNickname: String?,
    var senderImg: String?
){
    constructor(): this("","", "","","")
}
