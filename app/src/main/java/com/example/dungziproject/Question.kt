package com.example.dungziproject

import java.io.Serializable

data class Question(var questionId:String, var question:String): Serializable {
    constructor():this("","")
}