package com.supter.ui.main.purchase.detail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.supter.R
import com.supter.data.model.PotentialItem
import com.supter.data.db.entity.PurchaseEntity
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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

        purchaseEntity = args.purchaseEntity

        mBinding.purchase = purchaseEntity
        mBinding.description.text = requireContext().getString(R.string.description, purchaseEntity.description ?: "")

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
        initThinkingProgress()
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

    private fun setClickListeners() {

        mBinding.percentageView.setOnClickListener {
            animatePotential()
        }

//        mBinding.saveChanges.setOnClickListener {
//            with(mBinding) {
//                viewModel.updatePurchase(
//                    title.editText?.text.toString(),
//                    description.editText?.text.toString(),
//                    price.editText?.text.toString().toDouble(),
//                    purchaseEntity
//                )
//            }
//        }
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
                        showSuccessMessage()
                    }
                }
            })

        viewModel.timer.observe(viewLifecycleOwner, Observer { time ->
            mBinding.thinkingProgress.progress = time.toFloat()
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
                mBinding.thinkingTime.text = getString(R.string.zero_hours)
            } else {
                mBinding.thinkingTime.text =
                    getString(R.string.hours, currentProgressInHours.toString())
            }

        } else {
            mBinding.thinkingTime.text =
                getPrettyDate((currentProgressInPercentage * 60 * 60).toDouble())
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

    private fun animatePotential() {
        mBinding.percentageView.setPercentage(purchaseEntity.potential.roundToInt())
    }

    private fun showSuccessMessage() {
        Toasty.success(requireContext(), getString(R.string.successfully_updated)).show()
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

}
