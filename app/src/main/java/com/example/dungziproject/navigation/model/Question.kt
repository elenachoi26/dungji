package com.example.dungziproject.navigation.model

import java.io.Serializable

data class Question(var questionId:String, var question:String): Serializable {
    constructor():this("","")
}