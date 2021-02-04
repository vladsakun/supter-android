package com.supter.ui.main.purchase.detail

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.model.PotentialItem
import com.supter.data.response.ResultWrapper
import com.supter.data.response.purchase.DetailPurchaseResponse
import com.supter.data.response.purchase.QuestionsItem
import com.supter.data.response.purchase.UpdatePurchaseResponse
import com.supter.databinding.DetailPurchaseFragmentBinding
import com.supter.databinding.SelectImageAlertDialogBinding
import com.supter.ui.adapters.PotentialAdapter
import com.supter.ui.adapters.SimpleDividerItemDecorationLastExcluded
import com.supter.utils.*
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.lang.Exception


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

    private lateinit var answeredQuestions: MutableList<QuestionsItem>
    private lateinit var toDoQuestions: MutableList<QuestionsItem>

    companion object {
        val SEND_ANSWER_ACTION = "SEND_ANSWER_ACTION"

        val STRING_ANSWER_EXTRA = "STRING_ANSWER_EXTRA"
        val BOOLEAN_ANSWER_EXTRA = "BOOLEAN_ANSWER_EXTRA"
        val QUESTION_INDEX_IN_ARRAY_EXTRA = "QUESTION_INDEX_IN_ARRAY_EXTRA"
        val UPDATE_EXTRA = "UPDATE_EXTRA"

        val QUESTION_ID_EXTRA = "QUESTION_ID_EXTRA"

        fun newInstance() = DetailPurchaseFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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

        initThinkingProgress()

        initAvailabilityProgress()

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

        viewModel.purchaseEntity = purchaseEntity

        mBinding.purchase = purchaseEntity
        mBinding.description.editText?.setText(purchaseEntity.description ?: "")

        mBinding.potentialRing.progress = purchaseEntity.potential

        initPurchaseImage()

        mBinding.notifyChange()
    }

    private fun initPurchaseImage() {
        purchaseEntity.image?.let {
            if (it.contentEquals(getBoxByteArray(requireContext()))) {
                mBinding.purchaseImage.setImageResource(R.drawable.ic_add_photo)
            } else {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                mBinding.purchaseImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun setClickListeners() {

        with(mBinding) {

            save.setOnClickListener {
                purchaseEntity.title = title.editText?.text.toString()
                purchaseEntity.price = price.editText?.text.toString().toDouble()
                purchaseEntity.description = description.editText?.text.toString()
                purchaseEntity.link = link.editText?.text.toString()

                viewModel.updatePurchase(purchaseEntity)
            }

            purchaseImage.setOnClickListener {
                selectImage()
            }

            link.setEndIconOnClickListener {
                link.editText?.setText(getTextFromClipboard())
            }
        }
    }

    private fun selectImage() {

        val dialogBuilder = MaterialAlertDialogBuilder(requireActivity()).create()

        val dialogBinding = SelectImageAlertDialogBinding.inflate(requireActivity().layoutInflater)

        dialogBinding.onTakePhotoClick = View.OnClickListener {
            takePhoto()
            dialogBuilder.cancel()
        }

        dialogBinding.onChooseFromGalleryClick = View.OnClickListener {
            chooseFromGallery()
            dialogBuilder.cancel()
        }

        dialogBinding.onPasteClick = View.OnClickListener {
            pasteImage()
            dialogBuilder.cancel()
        }

        dialogBinding.onCancelClick = View.OnClickListener { dialogBuilder.dismiss() }
        dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuilder.setView(dialogBinding.root)
        dialogBuilder.setCancelable(true)

        dialogBuilder.show()
    }

    private fun bindObservers() {
        viewModel.getPurchaseFromApi(purchaseEntity).observe(viewLifecycleOwner,
            Observer<ResultWrapper<DetailPurchaseResponse>> { purchaseResponse ->
                when (purchaseResponse) {

                    is ResultWrapper.Success -> initQuestionsList(purchaseResponse.value)

                    is ResultWrapper.NetworkError -> showErrorMessage(requireContext().getString(R.string.no_internet_connection))

                    is ResultWrapper.GenericError -> showErrorMessage(
                        purchaseResponse.error?.message
                            ?: requireContext().getString(R.string.no_internet_connection)
                    )
                }
            })

        viewModel.updateResponseResultLiveData.observe(
            viewLifecycleOwner,
            Observer<ResultWrapper<UpdatePurchaseResponse>> { updateResult ->
                when (updateResult) {

                    is ResultWrapper.Success -> {
                        refreshView(convertDataItemToPurchaseEntity(updateResult.value.data))
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

        val currentProgressInHours: Float =
            (thinkingTimeInSeconds - currentTimeInSeconds).toFloat() / 60 / 60

        var thinkingText: String

        if (currentProgressInHours <= 0) {
            thinkingText = getPrettyDate(0)
        } else {
            thinkingText = getPrettyDate((currentProgressInHours / 24))
        }

        thinkingText += " left"

        mBinding.thinking.text = thinkingText

        val oneSecPercent: Float = 1 * 100 / (thinkingTimeInSeconds - createdAtInSeconds).toFloat()

        viewModel.timer(currentProgressInPercentage.toFloat(), oneSecPercent)
    }

    private fun initAvailabilityProgress() {

        val createdAtInSeconds =
            stringToDate(purchaseEntity.createdAt)?.time?.div(1000)!!

        viewModel.getUser().observe(viewLifecycleOwner, Observer { account ->
            if (account.period != null && account.incomeRemainder != null) {
                val maxAvailabilityTimeInDays =
                    daysRealPeriod(
                        account.period,
                        purchaseEntity.realPeriod,
                        account.salaryDay
                    ) // days

                val maxAvailabilityTimeInSeconds = maxAvailabilityTimeInDays * 24 * 60 * 60

                val timeBetweenCreatedAtTillToday =
                    System.currentTimeMillis() / 1000 - createdAtInSeconds // seconds

                var availabilityLeftTime =
                    maxAvailabilityTimeInSeconds - timeBetweenCreatedAtTillToday // seconds

                if (availabilityLeftTime < 0) {
                    availabilityLeftTime = 0f
                }

                val availabilityTimeText =
                    getPrettyDate(availabilityLeftTime / 60 / 60 / 24) + " left"
                mBinding.availability.text = availabilityTimeText

                val availabilityProgress =
                    100 * timeBetweenCreatedAtTillToday / maxAvailabilityTimeInSeconds

                mBinding.availabilityRing.progress = availabilityProgress
            }

        })

    }

    private fun initQuestionsList(detailPurchaseEntity: DetailPurchaseResponse) {

        answeredQuestions =
            detailPurchaseEntity.data.questions.filter { it.purchaseQuestion != null } as MutableList<QuestionsItem>

        toDoQuestions =
            detailPurchaseEntity.data.questions.filter { it.purchaseQuestion == null } as MutableList<QuestionsItem>

        val itemDecoration = SimpleDividerItemDecorationLastExcluded(10)

        if (toDoQuestions.isEmpty()) {
            mBinding.toDoBlock.isVisible = false
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
            initAnsweredQuestionsRecyclerView(convertQuestionListToPotentialItems(answeredQuestions))
        }

        hideQuestionsProgress()
    }

    private fun initAnsweredQuestionsRecyclerView(answeredPotentialItemList: MutableList<PotentialItem>) {

        donePotentialAdapter =
            PotentialAdapter(
                answeredPotentialItemList,
                true,
                requireActivity()
            )

        mBinding.doneRecyclerview.adapter = donePotentialAdapter
        mBinding.doneRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val itemDecoration = SimpleDividerItemDecorationLastExcluded(10)

        mBinding.doneRecyclerview.addItemDecoration(itemDecoration)
    }

    private fun hideQuestionsProgress() {
        mBinding.doneIncreasePotentialProgress.isVisible = false
        mBinding.toIncreasePotentialProgress.isVisible = false
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
            val questionIndexInArray = intent.getIntExtra(QUESTION_INDEX_IN_ARRAY_EXTRA, 0)

            val isUpdate = intent.getBooleanExtra(UPDATE_EXTRA, false)

            viewModel.sendAnswer(purchaseEntity.id, questionId, stringAnswer, booleanAnswer)
                .observe(viewLifecycleOwner, Observer { isUpdatedSuccessfully ->
                    requireContext()
                        .applicationContext
                        .sendBroadcast(Intent(PotentialAdapter.SUBMIT_ANSWER_ACTION).apply {
                            putExtra(PotentialAdapter.IS_SUBMIT_SUCCESS, isUpdatedSuccessfully)
                        })

                    if (isUpdatedSuccessfully) {

                        val answeredQuestion: QuestionsItem =
                            toDoQuestions.first { it.id == questionId }

                        toIncreasePotentialAdapter?.removeItemAt(questionIndexInArray)

                        if (toIncreasePotentialAdapter?.itemCount == 0) {
                            mBinding.toDoBlock.isVisible = false
                        }

                        val potentialItem = convertQuestionItemToPotentialItem(
                            false,
                            answeredQuestion,
                            stringAnswer
                        )

                        if (donePotentialAdapter == null) {
                            mBinding.doneBlock.isVisible = true
                            initAnsweredQuestionsRecyclerView(mutableListOf(potentialItem))
                        } else {
                            donePotentialAdapter?.addItem(potentialItem)
                        }
                    }

                    if (isUpdate) {
                        updateQuestionAdapters()
                    }

                })

        }
    }

    private fun showSuccessMessage() {
        Toasty.success(requireContext(), getString(R.string.successfully_updated)).show()
    }

    private fun showErrorMessage(message: String = requireContext().getString(R.string.no_internet_connection)) {
        Toasty.error(requireContext(), message).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> {
                    if (resultCode == RESULT_OK && data != null) {
                        val selectedImage = data.extras?.get("data") as Bitmap
                        mBinding.purchaseImage.setImageBitmap(selectedImage)

                        sendPurchaseImageOnServer(selectedImage)
                    }
                }

                1 -> {
                    if (resultCode == RESULT_OK && data != null) {
                        val selectedImage: Uri? = data.data

                        if (selectedImage != null) {

                            val bitmap = getBitmapFromUri(selectedImage)

                            bitmap?.let {
                                mBinding.purchaseImage.setImageBitmap(bitmap)

                                sendPurchaseImageOnServer(bitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        requireActivity().contentResolver,
                        uri
                    )
                )
            } else {
                // deprecated is Ok for android < 28
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )
            }
        } catch (e: Exception) {
            return null // it is not an image in clipboard
        }
    }

    private fun sendPurchaseImageOnServer(bitmap: Bitmap) {
        val f = PhotoManager.createFileFromBitmap(bitmap, requireContext())

        val requestBody = f.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body =
            MultipartBody.Part.createFormData("image", f.name, requestBody)

        viewModel.postPurchaseImage(body)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_purchase_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.delete) {
            MaterialAlertDialogBuilder(requireActivity())
                .setMessage(
                    requireActivity().getString(
                        R.string.are_you_sure_to_delete,
                        purchaseEntity.title
                    )
                )
                .setPositiveButton(R.string.delete) { dialog, which ->
                    dialog.dismiss()
                    viewModel.deletePurchase(purchaseEntity)
                    findNavController().navigateUp()
                }
                .setNegativeButton(R.string.no) { dialog, which ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()

            return true
        }

        return NavigationUI.onNavDestinationSelected(
            item,
            mBinding.root.findNavController()
        ) || super.onOptionsItemSelected(item)

    }

    private fun getTextFromClipboard(): String {
        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // If it does contain data, decide if you can handle the data.
        return if (!clipboard.hasPrimaryClip()) {
            ""
        } else if (!clipboard.primaryClipDescription!!.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
            // since the clipboard has data but it is not plain text
            ""
        } else {
            val item = clipboard.primaryClip?.getItemAt(0)

            item?.text.toString()
        }
    }

    private fun takePhoto() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, 0)
    }

    private fun chooseFromGallery() {
        val pickPictureIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(pickPictureIntent, 1)
    }

    private fun pasteImage() {
        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip: ClipData? = clipboard.primaryClip

        clip?.run {

            val item: ClipData.Item = getItemAt(0)

            val pasteUri: Uri? = item.uri

            pasteUri?.let {
                val bitmap = getBitmapFromUri(it)

                bitmap?.let {
                    mBinding.purchaseImage.setImageBitmap(bitmap)

                    sendPurchaseImageOnServer(bitmap)
                }
            }
        }
    }

}
