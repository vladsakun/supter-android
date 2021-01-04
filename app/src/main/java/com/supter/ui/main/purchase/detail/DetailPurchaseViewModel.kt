package com.supter.ui.main.purchase.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.ResultWrapper
import com.supter.data.response.purchase.PurchaseResponse
import com.supter.data.response.purchase.UpdatePurchaseResponse
import com.supter.repository.PurchaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailPurchaseViewModel @ViewModelInject constructor(
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    private val _updateResponseResultLiveData =
        MutableLiveData<ResultWrapper<UpdatePurchaseResponse>>()

    private val _timer = MutableLiveData(0.0f)
    val timer: LiveData<Float> get() = _timer

    val updateResponseResultLiveData: LiveData<ResultWrapper<UpdatePurchaseResponse>> get() = _updateResponseResultLiveData

    fun deletePurchase(purchaseEntity: PurchaseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            purchaseRepository.deletePurchase(purchaseEntity)
        }
    }

    fun timer(startProgress: Float, oneSecPercent: Float) {

        _timer.value = startProgress

        viewModelScope.launch(Dispatchers.IO) {

            while (true) {
                val progress = _timer.value!! + oneSecPercent
                _timer.postValue(progress)
                delay(1000)
            }

        }
    }

    fun getUser(): LiveData<UserEntity> {

        val userEntityMutableLiveData = MutableLiveData<UserEntity>()

        viewModelScope.launch(Dispatchers.IO) {
            purchaseRepository.getUserFlow().collect {
                userEntityMutableLiveData.postValue(it)
            }
        }

        return userEntityMutableLiveData
    }

    private val _purchase = MutableLiveData<ResultWrapper<PurchaseResponse>>()

    fun getPurchaseFromApi(purchaseEntity: PurchaseEntity): LiveData<ResultWrapper<PurchaseResponse>> {

        viewModelScope.launch(Dispatchers.IO) {
            _purchase.postValue(purchaseRepository.getPurchaseFromApiById(purchaseEntity))
        }

        return _purchase
    }

    val _isAnswerSuccessfullySubmitted = MutableLiveData<Boolean>()

    fun sendAnswer(purchaseId: Int, questionId: Int, answer: String): LiveData<Boolean> {
        viewModelScope.launch(Dispatchers.IO) {
            val response = purchaseRepository.sendAnswer(purchaseId, questionId, answer)
            _isAnswerSuccessfullySubmitted.postValue(response is ResultWrapper.Success)
        }
        return _isAnswerSuccessfullySubmitted
    }
}