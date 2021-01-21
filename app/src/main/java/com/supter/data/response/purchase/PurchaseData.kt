package com.supter.data.response.purchase

data class PurchaseData(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String?,
    val stage: String,
    val userId: Int,
    val potential: Float,
    val order: Int,
    val thinkingTime: String,
    val createdAt: String,
    val updatedAt: String,
    val link: String?,
    val image: String?
) {
    override fun toString(): String {
        return "PurchaseData(id=$id, title='$title', price=$price, description=$description, stage='$stage', userId=$userId, potential=$potential, order=$order, thinkingTime='$thinkingTime', createdAt='$createdAt', updatedAt='$updatedAt', link=$link, image=$image)"
    }
}