package com.example.dungziproject

data class TimeTable(
    var timeTableId:String,
    var title:String,
    var week:String,
    var startTime:String,
    var endTime:String
) {
    constructor():this("","","","","")

}