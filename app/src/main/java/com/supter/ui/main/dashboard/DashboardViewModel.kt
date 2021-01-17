package com.supter.ui.main.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.supter.data.body.ChangeStageBody
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.ResultWrapper
import com.supter.repository.PurchaseRepository
import com.supter.utils.STATUS_DECIDED
import com.supter.utils.STATUS_WANT
import com.supter.utils.convertAccountResponseToUserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.round

class DashboardViewModel @ViewModelInject constructor(
    private val repository: PurchaseRepository,
) : ViewModel() {

    private val TAG = "DashboardViewModel"

    private val _errorMessageMutableLiveData = MutableLiveData<String?>()
    val errorMessageLiveData get() = _errorMessageMutableLiveData

    fun upsertPurchaseList(purchaseList: List<PurchaseEntity>) {
        sendIdsList(purchaseList)
    }

    fun sendIdsList(purchaseList: List<PurchaseEntity>) {
        val idsList = mutableListOf<Int>()

        for (purchase in purchaseList) {
            idsList.add(purchase.id)
        }

        viewModelScope.launch(Dispatchers.IO) {
            val resp = repository.putPurchasesOrder(idsList)
            when (resp) {
                is ResultWrapper.Success -> {
                    repository.upsertPurchaseList(updatePurchasesData(purchaseList))
                }

                is ResultWrapper.GenericError -> {
                    _errorMessageMutableLiveData.postValue(resp.error?.message)
                }

                is ResultWrapper.NetworkError -> {
                    _errorMessageMutableLiveData.postValue(null)
                }
            }
        }

    }

    fun moveToAnotherStage(
        columnName: String,
        purchaseListOfColumn: List<PurchaseEntity>,
        purchaseEntity: PurchaseEntity
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changePurchaseStage(
                purchaseEntity.id,
                ChangeStageBody(
                    columnName,
                    purchaseListOfColumn.indexOf(purchaseEntity)
                )
            )
        }
    }

    private val _purchaseList = MutableLiveData<List<PurchaseEntity>>()

    fun getPurchaseLiveData(): LiveData<List<PurchaseEntity>> {

        userEntity?.let {
            return repository.getPurchaseList(it).asLiveData(Dispatchers.IO)
        }

        return _purchaseList

    }

    private fun updatePurchasesData(purchaseList: List<PurchaseEntity>): List<PurchaseEntity> {

        userEntity?.let { user ->

            if (user.incomeRemainder != null && user.period != null) {

                val newPurchaseList = mutableListOf<PurchaseEntity>()
                val purchaseListWithoutDoneAndSortedByStage = mutableListOf<PurchaseEntity>()

                val processList =
                    purchaseList.filter { it.stage == STATUS_DECIDED }.sortedBy { it.order }

                for (purchase in processList) {
                    purchaseListWithoutDoneAndSortedByStage.add(purchase)
                }

                val statusWant =
                    purchaseList.filter { it.stage == STATUS_WANT }.sortedBy { it.order }

                for (purchase in statusWant) {
                    purchaseListWithoutDoneAndSortedByStage.add(purchase)
                }

                for ((index, element) in purchaseListWithoutDoneAndSortedByStage.withIndex()) {
                    if (index == 0) {
                        val currentPeriod: Double = element.price / user.incomeRemainder

                        val productPeriod = rounder(currentPeriod)

                        val productRemind = BigDecimal(productPeriod - currentPeriod).setScale(
                            10,
                            RoundingMode.HALF_EVEN
                        ).toDouble()

                        element.remind = productRemind
                        element.realPeriod = productPeriod

                    } else {

                        val previousProduct = purchaseListWithoutDoneAndSortedByStage[index - 1]

                        val currentPeriod: Double =
                            element.price / user.incomeRemainder - previousProduct.remind

                        val productPeriod = rounder(currentPeriod)

                        val productRemind = BigDecimal(productPeriod - currentPeriod).setScale(
                            10,
                            RoundingMode.HALF_EVEN
                        ).toDouble()

                        element.remind = productRemind
                        element.realPeriod = productPeriod + previousProduct.realPeriod

                    }

                    newPurchaseList.add(element)
                }

                return newPurchaseList

            }

            return purchaseList

        }

        return purchaseList

    }

    private fun rounder(x: Double): Int {
        if (x == round(x)) {
            return x.toInt()
        }

        return ceil(x).toInt()
    }

    private val _user = MutableLiveData<UserEntity?>()

    fun getUser(): LiveData<UserEntity?> {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserFlow().collect {
                _user.postValue(it)
            }
        }
        return _user
    }

    var userEntity: UserEntity? = null
    suspend fun fetchUser(): UserEntity? {
        return withContext(Dispatchers.IO) {

            val accountResponse = repository.fetchUser()

            if (accountResponse is ResultWrapper.Success) {
                userEntity = convertAccountResponseToUserEntity(accountResponse.value)
                return@withContext userEntity
            } else {
                return@withContext null
            }

        }
    }

}