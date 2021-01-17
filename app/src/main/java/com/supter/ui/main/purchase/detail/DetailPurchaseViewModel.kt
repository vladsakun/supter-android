package com.supter.ui.main.purchase.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.ResultWrapper
import com.supter.data.response.purchase.DetailPurchaseResponse
import com.supter.data.response.purchase.UpdatePurchaseResponse
import com.supter.repository.PurchaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailPurchaseViewModel @ViewModelInject constructor(
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    private val _updateResponseResultLiveData =
        MutableLiveData<ResultWrapper<UpdatePurchaseResponse>>()

    val updateResponseResultLiveData: LiveData<ResultWrapper<UpdatePurchaseResponse>> get() = _updateResponseResultLiveData

    private val _timer = MutableLiveData(0.0f)
    val timer: LiveData<Float> get() = _timer

    fun deletePurchase(purchaseEntity: PurchaseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            purchaseRepository.deletePurchase(purchaseEntity)
        }
    }

    fun updatePurchase(purchaseEntity: PurchaseEntity){
        viewModelScope.launch(Dispatchers.IO){
            _updateResponseResultLiveData.postValue(purchaseRepository.updateRemotePurchase(purchaseEntity))
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

    private val _purchase = MutableLiveData<ResultWrapper<DetailPurchaseResponse>>()

    fun getPurchaseFromApi(purchaseEntity: PurchaseEntity): LiveData<ResultWrapper<DetailPurchaseResponse>> {

        viewModelScope.launch(Dispatchers.IO) {
            _purchase.postValue(purchaseRepository.getPurchaseFromApiById(purchaseEntity))
        }

        return _purchase
    }

    suspend fun fetchPurchase(purchaseEntity: PurchaseEntity):DetailPurchaseResponse?{
        return withContext(Dispatchers.IO){
            val resp = purchaseRepository.getPurchaseFromApiById(purchaseEntity)
            if(resp is ResultWrapper.Success){
                return@withContext resp.value
            }else{
                return@withContext null
            }
        }
    }

    val _isAnswerSuccessfullySubmitted = MutableLiveData<Boolean>()

    fun sendAnswer(purchaseId: Int, questionId: Int, stringAnswer:String?, booleanAnswer:Boolean):LiveData<Boolean>{

        if(stringAnswer == null){
            return sendBooleanAnswer(purchaseId, questionId, booleanAnswer)
        }else{
            return sendStringAnswer(purchaseId, questionId, stringAnswer)
        }

    }

    private fun sendStringAnswer(purchaseId: Int, questionId: Int, answer: String): LiveData<Boolean> {
        viewModelScope.launch(Dispatchers.IO) {
            val response = purchaseRepository.sendStringAnswer(purchaseId, questionId, answer)
            _isAnswerSuccessfullySubmitted.postValue(response is ResultWrapper.Success)
        }
        return _isAnswerSuccessfullySubmitted
    }

    private fun sendBooleanAnswer(purchaseId: Int, questionId: Int, answer: Boolean): LiveData<Boolean> {
        viewModelScope.launch(Dispatchers.IO) {
            val response = purchaseRepository.sendBooleanAnswer(purchaseId, questionId, answer)
            _isAnswerSuccessfullySubmitted.postValue(response is ResultWrapper.Success)
        }
        return _isAnswerSuccessfullySubmitted
    }
}