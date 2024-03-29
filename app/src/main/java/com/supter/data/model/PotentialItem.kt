package com.supter.data.model

data class PotentialItem (
    val isDone:Boolean,
    val title:String,
    val answer:String?,
    val isTrue:Boolean?,
    val questionId:Int,
    val questionType:String?, // text boolean
)