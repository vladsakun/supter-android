package com.supter.data.response.purchase

data class PurchaseData(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String?,
    val stage: String,
    val user: UserData,
    val potential: Float,
    val order: Int,
    val thinkingTime:String,
    val createdAt:String,
    val link:String?,
){
	override fun toString(): String {
		return "PurchaseData(id=$id, title='$title', price=$price, description=$description, stage='$stage', user=$user, potential=$potential, order=$order, thinkingTime='$thinkingTime', createdAt='$createdAt')"
	}
}