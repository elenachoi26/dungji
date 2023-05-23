package com.example.dungziproject

import java.time.LocalDateTime

data class Message(
    var message: String?,
    var sendId: String?,
    var sendTime: LocalDateTime?
){
    constructor(): this("","", LocalDateTime.now())
}
