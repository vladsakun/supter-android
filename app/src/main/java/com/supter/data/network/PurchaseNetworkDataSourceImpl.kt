package com.supter.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.supter.data.body.UserParams
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.exceptions.NoConnectivityException
import com.supter.data.response.ErrorResponse
import com.supter.data.response.Resp
import com.supter.data.response.ResponseWrapper
import com.supter.data.response.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PurchaseNetworkDataSourceImpl(
    private val api: Api
) : PurchaseNetworkDataSource {

    val CONNECTIVITY_TAG = "Connectivity"
    private val TAG = "PurchaseNetworkDataSour"

    private val _fetchedPurchaseList = MutableLiveData<List<PurchaseEntity>>()
    override val fetchedPurchaseList: LiveData<List<PurchaseEntity>>
        get() = _fetchedPurchaseList

    //Fetch popular movie list
    override suspend fun fetchPurchaseList() {
        try {
            val list = ArrayList<PurchaseEntity>()
//
//            list.add(PurchaseEntity(1, 2000.0, "Laptop", null))
//            list.add(PurchaseEntity(2, 1000.0, "Chair", null))
//            list.add(PurchaseEntity(3, 1500.0, "Table", null))
//            list.add(PurchaseEntity(1, 2000.0, "Smartphone", null))
//            list.add(PurchaseEntity(3, 2500.0, "Car", null))

            _fetchedPurchaseList.postValue(list)

        } catch (e: NoConnectivityException) {
            Log.e(CONNECTIVITY_TAG, "No internet connection", e)
        }
    }

    private val _registrationResp = MutableLiveData<Event<Resp>>()
    override val registrationResp: LiveData<Event<Resp>>
        get() = _registrationResp

    override fun register(name: String, email: String, password: String) {
        try {

            apiCall(_registrationResp, api.register(UserParams(name, email, password)))

        } catch (e: NoConnectivityException) {
            Log.e(CONNECTIVITY_TAG, "register: ", e)
        }
    }

    override suspend fun registerWithCoroutines(
        name: String,
        email: String,
        password: String
    ): ResultWrapper<Resp> {
        return safeApiCall(Dispatchers.IO) { api.registerUser(UserParams(name, email, password)) }
    }

    fun <T> apiCall(liveData: MutableLiveData<Event<T>>, call: Call<ResponseWrapper<T>>) {

        liveData.postValue(Event.loading())

        call.enqueue(object : Callback<ResponseWrapper<T>> {
            override fun onResponse(
                call: Call<ResponseWrapper<T>>,
                response: Response<ResponseWrapper<T>>
            ) {
                if (response.isSuccessful) {
                    liveData.postValue(Event.success(response.body()?.data))
                } else {
                    Log.d(TAG, "onResponse: " + response.errorBody()?.source())
                    response.errorBody()?.source()?.let {
                        val moshiAdapter =
                            Moshi.Builder().build().adapter(ErrorResponse::class.java)
                        val errorResponse = moshiAdapter.fromJson(it)
                        Log.d(TAG, "onResponse: " + errorResponse?.message)
                    }
                    liveData.postValue(Event.genericError(""))
                }
            }

            override fun onFailure(call: Call<ResponseWrapper<T>>, t: Throwable) {
                liveData.postValue(Event.error(null))
            }

        })
    }

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResultWrapper.GenericError(null, null)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            val gson = Gson()
            val errorResponse = gson.fromJson(
                throwable.response()?.errorBody()?.charStream()?.readText(),
                ErrorResponse::class.java
            )
            return errorResponse
//            throwable.response()?.errorBody()?.charStream()?.readText().let {
//                val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
//                moshiAdapter.fromJson(it)
//            }
        } catch (exception: Exception) {
            null
        }
    }
}