package com.supter.data.model

data class PotentialItem (
    val isDone:Boolean,
    val title:String,
    val answer:String?,
    val questionId:Int,
    val questionType:Int, // 1-String 2- Boolean
)