package com.supter.ui.main.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.ResultWrapper
import com.supter.repository.PurchaseRepository
import com.supter.utils.STATUS_DONE
import com.supter.utils.STATUS_PROCESS
import com.supter.utils.STATUS_WANT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.round

class DashboardViewModel @ViewModelInject constructor(
    private val purchaseRepository: PurchaseRepository,
) : ViewModel() {

    private val TAG = "DashboardViewModel"

    private val _errorMessageMutableLiveData = MutableLiveData<String?>()
    val errorMessageLiveData get() = _errorMessageMutableLiveData

    fun updatePurchase(dragItem: PurchaseEntity, toColumn: Int) {
        when (toColumn) {
            0 -> dragItem.stage = STATUS_WANT
            1 -> dragItem.stage = STATUS_PROCESS
            2 -> dragItem.stage = STATUS_DONE
        }

        viewModelScope.launch(Dispatchers.IO) {
            purchaseRepository.upsertPurchase(dragItem)
        }
    }

    fun upsertPurchaseList(purchaseList: List<PurchaseEntity>) {
        sendIdsList(purchaseList)
    }

    fun sendIdsList(purchaseList: List<PurchaseEntity>) {
        val idsList = mutableListOf<Int>()

        for (purchase in purchaseList) {
            idsList.add(purchase.id)
        }

        viewModelScope.launch(Dispatchers.IO) {
            val resp = purchaseRepository.putPurchasesOrder(idsList)
            when (resp) {
                is ResultWrapper.Success -> {
                    purchaseRepository.upsertPurchaseList(updatePurchasesData(purchaseList))
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

    private val _purchaseList = MutableLiveData<List<PurchaseEntity>>()
    private val purchaseList: LiveData<List<PurchaseEntity>> get() = _purchaseList

    fun getPurchaseLiveData(): LiveData<List<PurchaseEntity>> {

//        viewModelScope.launch(Dispatchers.IO) {
//            val purchaseFlow =
//                purchaseRepository.getPurchaseList()
//                    .map {
//                        updatePurchasesData(it)
//                    }.asLiveData(Dispatchers.IO)
//        }
        userEntity?.let { user ->
            return purchaseRepository.getPurchaseList(user).asLiveData(Dispatchers.IO)
        }

        return _purchaseList

    }

    fun updatePurchasesData(purchaseList: List<PurchaseEntity>): List<PurchaseEntity> {

        userEntity?.let { user ->

            if (user.incomeRemainder != null && user.period != null) {

                val newPurchaseList = mutableListOf<PurchaseEntity>()
                val purchaseListWithoutDoneAndSortedByStage = mutableListOf<PurchaseEntity>()

                val processList =
                    purchaseList.filter { it.stage == STATUS_PROCESS }.sortedBy { it.order }

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
            purchaseRepository.getUserFlow().collect {
                _user.postValue(it)
            }
        }
        return _user
    }

    var userEntity: UserEntity? = null
    suspend fun getUserSuspend(): UserEntity? {
        return withContext(Dispatchers.IO) {
            userEntity = purchaseRepository.getUser()
            return@withContext userEntity
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}