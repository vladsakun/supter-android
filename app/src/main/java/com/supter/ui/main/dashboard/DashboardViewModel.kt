package com.supter.ui.main.dashboard

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.supter.data.body.ChangeStageBody
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.ResultWrapper
import com.supter.data.response.account.AccountResponse
import com.supter.repository.PurchaseRepository
import com.supter.utils.convertAccountResponseToUserEntity
import com.supter.utils.updatePurchasesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel @ViewModelInject constructor(
    private val repository: PurchaseRepository,
) : ViewModel() {

    private val TAG = "DashboardViewModel"

    private val _errorMessageMutableLiveData = MutableLiveData<String?>()
    val errorMessageLiveData get() = _errorMessageMutableLiveData

    fun upsertPurchaseList(purchaseList: List<PurchaseEntity>, stage: String) {
        sendIdsList(purchaseList, stage)
    }

    fun sendIdsList(purchaseList: List<PurchaseEntity>, stage:String) {
        val idsList = mutableListOf<Int>()

        for (purchase in purchaseList) {
            idsList.add(purchase.id)
        }

        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "sendIdsList: $idsList")
            val resp = repository.putPurchasesOrder(idsList, stage)
            when (resp) {
                is ResultWrapper.Success -> {
                    repository.upsertPurchaseList(updatePurchasesData(purchaseList, userEntity))
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

    private val _user = MutableLiveData<UserEntity?>()

    fun getUser(): LiveData<UserEntity?> {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserFlow().collect {
                _user.postValue(it)
            }
        }
        return _user
    }

    private var userEntity: UserEntity? = null

    suspend fun fetchUser(): ResultWrapper<AccountResponse?>? {
        return withContext(Dispatchers.IO) {
            val resp = repository.fetchUser()
            if (resp is ResultWrapper.Success) {
                userEntity = convertAccountResponseToUserEntity(resp.value)
            }
            return@withContext resp
        }
    }
}