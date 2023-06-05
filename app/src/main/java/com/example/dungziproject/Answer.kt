package com.example.dungziproject

import java.io.Serializable

data class Answer(var nickname:String, var answer:String, var userId:String, var questionId:String, var answerId:String): Serializable {
    constructor():this("", "","", "","")
}