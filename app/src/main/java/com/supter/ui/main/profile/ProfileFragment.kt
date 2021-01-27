package com.supter.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.supter.R
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.ResultWrapper
import com.supter.databinding.FragmentProfileBinding
import com.supter.ui.auth.LoginActivity
import com.supter.utils.MONTH_IN_DAYS
import com.supter.utils.NotificationWorker
import com.supter.utils.NotificationWorker.Companion.NOTIFICATION_ID
import com.supter.utils.NotificationWorker.Companion.NOTIFICATION_WORK
import com.supter.utils.SystemUtils
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val TAG = "ProfileFragment"

    private val mBinding: FragmentProfileBinding by viewBinding(createMethod = CreateMethod.INFLATE)

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mBinding.numberPicker.maxValue = 27
        mBinding.numberPicker.minValue = 1
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        bindObservers()
        bindClickListeners()
    }

    private fun bindObservers() {

        viewModel.getUser().observe(viewLifecycleOwner, Observer {
            performUserData(it)
        })

        viewModel.accountResponse.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    showSuccessToast()
                }

                is ResultWrapper.GenericError -> {
                    showErrorMessage(
                        result.error?.message
                            ?: requireContext().getString(R.string.no_internet_connection)
                    )
                }

                is ResultWrapper.NetworkError -> {
                    showErrorMessage(
                        requireContext().getString(R.string.no_internet_connection)
                    )
                }
            }
        })
    }

    private fun performUserData(user: UserEntity?) {
        user?.let {
            mBinding.run {
                incomeRemainder.editText?.setText(it.incomeRemainder.toString())
                period.editText?.setText(it.period.toString())
                balance.editText?.setText(it.balance.toString())
                name.editText?.setText(it.name)
                numberPicker.value = it.salaryDay
            }
            setNotificationDelay(it)
        }
    }

    private fun setNotificationDelay(userEntity: UserEntity) {

        val calendar = Calendar.getInstance()

        Log.d(TAG, "dayOfMonth ${calendar.get(Calendar.DAY_OF_MONTH)} salaryDay: ${userEntity.salaryDay}")

        if (calendar.get(Calendar.DAY_OF_MONTH) > userEntity.salaryDay) {
            calendar.add(Calendar.MONTH, 1)
        }

        calendar.set(Calendar.DAY_OF_MONTH, userEntity.salaryDay)


        val customTime = calendar.timeInMillis
        val currentTime = System.currentTimeMillis()
        val delay = customTime - currentTime

        Log.d(TAG, "customTime: $customTime currentTime $currentTime ")

        val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()

        scheduleNotification(delay, data)

    }

    private fun scheduleNotification(delay: Long, data: Data) {
        Log.d(TAG, "scheduleNotification: delay: $delay")
        val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(requireContext())
        instanceWorkManager.beginUniqueWork(NOTIFICATION_WORK, ExistingWorkPolicy.REPLACE, notificationWork).enqueue()
    }

    private fun bindClickListeners() {

        mBinding.save.setOnClickListener {
            viewModel.upsertUser(
                mBinding.name.editText?.text.toString(),
                mBinding.incomeRemainder.editText?.text.toString().toFloat(),
                mBinding.balance.editText?.text.toString().toFloat(),
                mBinding.period.editText?.text.toString().toFloat(),
                mBinding.numberPicker.value
            )
        }

        mBinding.incomeRemainder.setEndIconOnClickListener {
            Toasty.info(requireContext(), getString(R.string.hint_income_remainder)).show()
        }

        mBinding.period.setEndIconOnClickListener {
            Toasty.info(requireContext(), getString(R.string.hint_period)).show()
        }

        mBinding.balance.setEndIconOnClickListener {
            Toasty.info(requireContext(), getString(R.string.hint_balance)).show()
        }

        mBinding.monthly.setOnClickListener {
            mBinding.period.editText?.setText(MONTH_IN_DAYS)
        }
    }

    private fun showErrorMessage(message: String) {
        Toasty.error(requireContext(), message).show()
    }

    private fun showSuccessToast() {
        Toasty.success(requireContext(), requireContext().getString(R.string.successfully_updated))
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logout()
            16908332 -> findNavController().navigateUp()
        }
        return true
    }

    private fun logout() {
        SystemUtils.deleteToken(requireContext().applicationContext)
        viewModel.logout()
        requireContext().startActivity(LoginActivity.getStartIntent(requireContext()))
    }

}