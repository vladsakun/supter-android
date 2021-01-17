package com.supter.ui.main.purchase.detail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.model.PotentialItem
import com.supter.data.response.ResultWrapper
import com.supter.data.response.purchase.DetailPurchaseResponse
import com.supter.data.response.purchase.QuestionsItem
import com.supter.data.response.purchase.UpdatePurchaseResponse
import com.supter.databinding.DetailPurchaseFragmentBinding
import com.supter.ui.adapters.PotentialAdapter
import com.supter.ui.adapters.SimpleDividerItemDecorationLastExcluded
import com.supter.utils.ScopedFragment
import com.supter.utils.getPrettyDate
import com.supter.utils.stringToDate
import com.supter.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailPurchaseFragment : ScopedFragment() {

    private val TAG = "DetailPurchaseFragment"

    private var _binding: DetailPurchaseFragmentBinding? = null
    private val mBinding get() = _binding!!

    val args: DetailPurchaseFragmentArgs by navArgs()

    private val viewModel: DetailPurchaseViewModel by viewModels()

    private lateinit var purchaseEntity: PurchaseEntity
    private var toIncreasePotentialAdapter: PotentialAdapter? = null
    private var donePotentialAdapter: PotentialAdapter? = null
    private var areBigRingsVisible = true
    private val duration = 300L
    private val interpolator = AccelerateDecelerateInterpolator()

    companion object {
        val SEND_ANSWER_ACTION = "SEND_ANSWER_ACTION"

        val STRING_ANSWER_EXTRA = "STRING_ANSWER_EXTRA"
        val BOOLEAN_ANSWER_EXTRA = "BOOLEAN_ANSWER_EXTRA"
        val UPDATE_EXTRA = "UPDATE_EXTRA"

        val QUESTION_ID_EXTRA = "QUESTION_ID_EXTRA"

        fun newInstance() = DetailPurchaseFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_medium).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DetailPurchaseFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshView(args.purchaseEntity)

        bindViews()
        setClickListeners()
    }

    override fun onStart() {
        super.onStart()
        startAnswerBR()
    }

    override fun onStop() {
        super.onStop()
        stopAnswerBR()
    }

    private fun bindViews() {
        bindObservers()

        mBinding.link.setOnClickListener {
            if (purchaseEntity.link != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(purchaseEntity.link))
                requireContext().startActivity(intent)
            } else {
                Toasty.info(requireContext(), getString(R.string.link_is_empty)).show()
            }
        }
    }

    private fun refreshView(newPurchaseEntity: PurchaseEntity) {
        purchaseEntity = newPurchaseEntity
        mBinding.purchase = newPurchaseEntity
        mBinding.description.editText?.setText(purchaseEntity.description ?: "")

        mBinding.potentialRing.progress = purchaseEntity.potential
        mBinding.secondaryPotentialRing.progress = purchaseEntity.potential

        mBinding.notifyChange()

        initThinkingProgress()

    }

    private fun setClickListeners() {

        mBinding.save.setOnClickListener {
            with(mBinding) {
                purchaseEntity.title = title.editText?.text.toString()
                purchaseEntity.description = description.editText?.text.toString()
                purchaseEntity.price = price.editText?.text.toString().toDouble()

                viewModel.updatePurchase(purchaseEntity)
            }
        }

        mBinding.ringsParent.setOnClickListener {
            onRingsClick()
        }
    }

    private fun bindObservers() {
        viewModel.getPurchaseFromApi(purchaseEntity).observe(viewLifecycleOwner,
            Observer<ResultWrapper<DetailPurchaseResponse>> { purchaseResponse ->
                if (purchaseResponse is ResultWrapper.Success) {
                    initQuestionsList(purchaseResponse.value)
                }
            })

        viewModel.updateResponseResultLiveData.observe(
            viewLifecycleOwner,
            Observer<ResultWrapper<UpdatePurchaseResponse>> { updateResult ->
                when (updateResult) {
                    is ResultWrapper.Success -> {
                        refreshView(updateResult.value.data)
                        showSuccessMessage()
                    }
                    is ResultWrapper.GenericError -> {
                        showErrorMessage(
                            updateResult.error?.message
                                ?: requireContext().getString(R.string.no_internet_connection)
                        )
                    }
                    is ResultWrapper.NetworkError -> {
                        showErrorMessage()
                    }
                }
            })

        viewModel.timer.observe(viewLifecycleOwner, Observer { time ->
            mBinding.thinkingRing.progress = time.toFloat()
            mBinding.secondaryThinkingRing.progress = time.toFloat()
        })
    }

    private fun updateQuestionAdapters() {
        launch {
            val purchase = viewModel.fetchPurchase(purchaseEntity)
            if (purchase != null) {

                val answeredQuestions =
                    purchase.data.questions.filter { it.purchaseQuestion != null }
                val toDoQuestions =
                    purchase.data.questions.filter { it.purchaseQuestion == null }

                donePotentialAdapter?.updateItems(
                    convertQuestionListToPotentialItems(
                        answeredQuestions
                    )
                )
                toIncreasePotentialAdapter?.updateItems(
                    convertQuestionListToPotentialItems(
                        toDoQuestions
                    )
                )
            }
        }
    }

    private fun initThinkingProgress() {

        val thinkingTimeInSeconds =
            stringToDate(purchaseEntity.thinkingTime)?.time?.div(1000)!!

        val createdAtInSeconds =
            stringToDate(purchaseEntity.createdAt)?.time?.div(1000)!!

        val currentTimeInSeconds = (System.currentTimeMillis() / 1000)

        val currentProgressInPercentage: Long =
            ((currentTimeInSeconds - createdAtInSeconds) * 100) / (thinkingTimeInSeconds - createdAtInSeconds)

        val currentProgressInHours = (thinkingTimeInSeconds - currentTimeInSeconds) / 60 / 60

        if (currentProgressInHours < 24) {

            if (currentProgressInHours <= 0) {
                mBinding.thinking.text = getString(R.string.zero_hours)
            } else {
                mBinding.thinking.text =
                    getString(R.string.hours, currentProgressInHours.toString())
            }

        } else {
            mBinding.thinking.text =
                getPrettyDate((currentProgressInHours / 24).toDouble())
        }

        val oneSecPercent: Float = 1 * 100 / (thinkingTimeInSeconds - createdAtInSeconds).toFloat()

        viewModel.timer(currentProgressInPercentage.toFloat(), oneSecPercent)
    }

    private fun initQuestionsList(detailPurchaseEntity: DetailPurchaseResponse) {

        val answeredQuestions =
            detailPurchaseEntity.data.questions.filter { it.purchaseQuestion != null }
        val toDoQuestions =
            detailPurchaseEntity.data.questions.filter { it.purchaseQuestion == null }

        val itemDecoration = SimpleDividerItemDecorationLastExcluded(10)

        if (toDoQuestions.isEmpty()) {
            mBinding.wellDoneToIncrease.isVisible = true
        } else {
            val toDoPotentialItemList = convertQuestionListToPotentialItems(toDoQuestions)

            toIncreasePotentialAdapter =
                PotentialAdapter(
                    toDoPotentialItemList,
                    false,
                    requireActivity()
                )

            mBinding.toIncreasePotentialRecyclerview.adapter = toIncreasePotentialAdapter
            mBinding.toIncreasePotentialRecyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            mBinding.toIncreasePotentialRecyclerview.addItemDecoration(itemDecoration)
        }

        if (answeredQuestions.isEmpty()) {
            mBinding.doneBlock.isVisible = false
        } else {
            val answeredPotentialItemList = convertQuestionListToPotentialItems(answeredQuestions)

            donePotentialAdapter =
                PotentialAdapter(
                    answeredPotentialItemList,
                    true,
                    requireActivity()
                )

            mBinding.doneRecyclerview.adapter = donePotentialAdapter
            mBinding.doneRecyclerview.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            mBinding.doneRecyclerview.addItemDecoration(itemDecoration)
        }

        hideQuestionsProgress()
    }

    private fun hideQuestionsProgress() {
        mBinding.doneIncreasePotentialProgress.isVisible = false
        mBinding.toIncreasePotentialProgress.isVisible = false
    }

    private fun convertQuestionListToPotentialItems(questionsItemList: List<QuestionsItem>?): MutableList<PotentialItem> {

        val potentialItemsListResult = mutableListOf<PotentialItem>()

        if (questionsItemList != null) {
            for (questionItem in questionsItemList) {
                potentialItemsListResult.add(
                    PotentialItem(
                        true,
                        questionItem.title,
                        questionItem.purchaseQuestion?.text,
                        questionItem.id,
                        1
                    )
                )
            }
        }

        return potentialItemsListResult
    }

    private fun startAnswerBR() {
        requireContext().applicationContext.registerReceiver(
            answerBroadcastReceiver, IntentFilter(
                SEND_ANSWER_ACTION
            )
        )
    }

    private fun stopAnswerBR() {
        requireContext().applicationContext.unregisterReceiver(answerBroadcastReceiver)
    }

    private val answerBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val stringAnswer: String? = intent.getStringExtra(STRING_ANSWER_EXTRA)
            val booleanAnswer = intent.getBooleanExtra(BOOLEAN_ANSWER_EXTRA, false)
            val questionId = intent.getIntExtra(QUESTION_ID_EXTRA, 0)

            val isUpdate = intent.getBooleanExtra(UPDATE_EXTRA, false)

            if (isUpdate) {
                updateQuestionAdapters()
                return
            }

            viewModel.sendAnswer(purchaseEntity.id, questionId, stringAnswer, booleanAnswer)
                .observe(viewLifecycleOwner, Observer {
                    requireContext()
                        .applicationContext
                        .sendBroadcast(Intent(PotentialAdapter.SUBMIT_ANSWER_ACTION).apply {
                            putExtra(PotentialAdapter.IS_SUBMIT_SUCCESS, it)
                        })
                })

        }
    }

    private fun showSuccessMessage() {
        Toasty.success(requireContext(), getString(R.string.successfully_updated)).show()
    }

    private fun showErrorMessage(message: String = requireContext().getString(R.string.no_internet_connection)) {
        Toasty.error(requireContext(), message).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_purchase_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.delete) {
            viewModel.deletePurchase(purchaseEntity)
            findNavController().navigateUp()
            return true
        }

        return NavigationUI.onNavDestinationSelected(
            item,
            mBinding.root.findNavController()
        ) || super.onOptionsItemSelected(item)

    }

    fun onRingsClick() {
        if (areBigRingsVisible) {
            showSmallRings()
        } else {
            showBigRings()
        }
        areBigRingsVisible = !areBigRingsVisible
    }

    private fun showSmallRings() {
        val views = listOf(
            mBinding.potentialRing,
            mBinding.thinkingRing,
            mBinding.availabilityRing,
            mBinding.purchaseImage,
            mBinding.percentImage,
            mBinding.potentialImage,
            mBinding.dollar
        )

        for ((index, view) in views.withIndex()) {
            if (index == views.size - 1) {
                val animation = reduceScaleAnimation()
                animation.setAnimationListener(object:Animation.AnimationListener{
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        val smallRingsViews = listOf(
                            mBinding.secondaryPotentialRing,
                            mBinding.secondaryThinkingRing,
                            mBinding.secondaryAvailabilityRing,
                            mBinding.potential,
                            mBinding.potentialHint,
                            mBinding.thinking,
                            mBinding.thinkingHint,
                            mBinding.availability,
                            mBinding.availabilityHint
                        )

                        for(smallRingView in smallRingsViews){
                            smallRingView.startAnimation(increaseScaleAnimation())
                        }
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }

                })
                view.startAnimation(animation)
            }else{
                view.startAnimation(reduceScaleAnimation())
            }
        }
    }

    private fun reduceScaleAnimation(): ScaleAnimation {
        val scaleAnimation = ScaleAnimation(
            1f,
            0f,
            1f,
            0f,
            ScaleAnimation.RELATIVE_TO_SELF,
            0.5f,
            ScaleAnimation.RELATIVE_TO_SELF,
            0.5f
        )
        scaleAnimation.interpolator = interpolator
        scaleAnimation.duration = duration
        scaleAnimation.fillAfter = true
        return scaleAnimation
    }

    private fun increaseScaleAnimation(): ScaleAnimation {
        val reduceScaleAnimation = ScaleAnimation(
            0f,
            1f,
            0f,
            1f,
            ScaleAnimation.RELATIVE_TO_SELF,
            0.5f,
            ScaleAnimation.RELATIVE_TO_SELF,
            0.5f
        )
        reduceScaleAnimation.interpolator = interpolator
        reduceScaleAnimation.duration = duration
        reduceScaleAnimation.fillAfter = true
        return reduceScaleAnimation
    }

    private fun showBigRings() {
        val smallRingsViews = listOf(
            mBinding.secondaryPotentialRing,
            mBinding.secondaryThinkingRing,
            mBinding.secondaryAvailabilityRing,
            mBinding.potential,
            mBinding.potentialHint,
            mBinding.thinking,
            mBinding.thinkingHint,
            mBinding.availability,
            mBinding.availabilityHint
        )

        for ((index, smallView) in smallRingsViews.withIndex()) {
            if (index == smallRingsViews.size - 1) {
                val animation = reduceScaleAnimation()
                animation.setAnimationListener(object:Animation.AnimationListener{
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        val views = listOf(
                            mBinding.potentialRing,
                            mBinding.thinkingRing,
                            mBinding.availabilityRing,
                            mBinding.purchaseImage,
                            mBinding.percentImage,
                            mBinding.potentialImage,
                            mBinding.dollar
                        )

                        for(view in views){
                            view.startAnimation(increaseScaleAnimation())
                        }
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }

                })
                smallView.startAnimation(animation)
            }else{
                smallView.startAnimation(reduceScaleAnimation())
            }
        }
    }

}
