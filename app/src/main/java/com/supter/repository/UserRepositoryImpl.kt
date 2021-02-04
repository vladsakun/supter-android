package com.supter.repository

import android.content.Context
import com.supter.data.body.AccountBody
import com.supter.data.db.PurchaseDao
import com.supter.data.db.entity.UserEntity
import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.response.account.AccountResponse
import com.supter.data.response.account.LoginResponse
import com.supter.data.response.account.RegistrationResponse
import com.supter.data.response.ResultWrapper
import com.supter.utils.SystemUtils
import com.supter.utils.SystemUtils.Companion.saveToken
import com.supter.utils.convertAccountResponseToUserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext var context: Context,
    var dao: PurchaseDao,
    var networkDataSource: PurchaseNetworkDataSource,
) : UserRepository {

    init {
        context = context.applicationContext
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): ResultWrapper<RegistrationResponse> {
        return networkDataSource.registerWithCoroutines(name, email, password)
    }

    override suspend fun login(username: String, password: String):
            ResultWrapper<LoginResponse> {
        val response = networkDataSource.login(username, password)

        if (response is ResultWrapper.Success) {
            saveToken(context, response.value.accessToken)
        }

        return response
    }

    override fun getUser(): Flow<UserEntity?> {
        return dao.getUserFlow()
    }

    override fun upsertUser(userEntity: UserEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.upsertUser(userEntity)
        }
    }

    override suspend fun putUser(
        name: String,
        incomeRemainder: Float,
        savings: Float,
        period: Float,
        salaryDay: Int,
    ): ResultWrapper<AccountResponse> {

        val account = networkDataSource.putUser(
            AccountBody(name, incomeRemainder, savings, period, salaryDay)
        )

        if (account is ResultWrapper.Success) {
            upsertUser(
                convertAccountResponseToUserEntity(account.value)
            )
        }

        return account
    }

    override fun clearDB() {
        GlobalScope.launch(Dispatchers.IO) {
            dao.clearUserTable()
            dao.clearPurchaseTable()
        }
    }

}