package com.supter.data.response.purchase

data class DetailPurchaseResponse(
    val data: Data
)

data class Data(
    val createdAt: String,
    val stage: String,
    val price: String,
    val questions: List<QuestionsItem>,
    val description: String?,
    val id: Int,
    val title: String,
    val potential: Double,
    val userId: Int,
    val thinkingTime: String,
    val order: Int,
    val updatedAt: String
)

data class QuestionsItem(
    val purchaseQuestion: PurchaseQuestion?,
    val id: Int,
    val title: String,
    val type: String
)

data class PurchaseQuestion(
    val createdAt: String,
    val questionId: Int,
    val purchaseId: Int,
    val id: Int,
    val text: String?,
    val isTrue: Boolean?,
    val updatedAt: String,
)

