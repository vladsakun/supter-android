package com.supter.ui.adapters

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.supter.R
import com.supter.data.model.PotentialItem
import com.supter.ui.main.purchase.detail.DetailPurchaseFragment
import com.supter.utils.logException
import com.supter.views.LoadingButton
import es.dmoral.toasty.Toasty

class PotentialAdapter(
    val potentialItemList: MutableList<PotentialItem>,
    val isDone: Boolean,
    val activity: Activity,
) : RecyclerView.Adapter<PotentialAdapter.PotentialViewHolder>() {

    companion object {
        val SUBMIT_ANSWER_ACTION = "SUBMIT_ANSWER_ACTION"
        val IS_SUBMIT_SUCCESS = "IS_SUBMIT_SUCCESS"
    }

    private var mSubmitAnswerResponseBR: BroadcastReceiver? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PotentialViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.potential_item, parent, false)
        return PotentialViewHolder(view)
    }

    override fun onBindViewHolder(holder: PotentialViewHolder, position: Int) {
        holder.isDone.isVisible = isDone

        val potentialItem = potentialItemList[position]
        holder.title.text = potentialItem.title

        holder.itemView.setOnClickListener {
            val dialogBuilder = MaterialAlertDialogBuilder(activity).create()
            val layoutInflater = activity.layoutInflater

            val dialogView:View

            if(potentialItem.questionType == 1){
                dialogView = layoutInflater.inflate(R.layout.string_question_alert_dialog, null)

                val questionTitle = dialogView.findViewById<TextView>(R.id.question_title)
                questionTitle.text = potentialItem.title

                val answerEditText = dialogView.findViewById<TextInputLayout>(R.id.question_answer)

                val submitBtn = dialogView.findViewById<LoadingButton>(R.id.submit)
                submitBtn.setOnClickListener {

                    val answer = answerEditText.editText?.text.toString().trim()

                    if (answer.isBlank()) {
                        Toasty.warning(activity, activity.getString(R.string.answer_cant_be_blank))
                    } else {

                        mSubmitAnswerResponseBR = object : BroadcastReceiver() {
                            override fun onReceive(context: Context, intent: Intent) {
                                submitBtn.cancelLoading()

                                val isSuccessSubmitted =
                                    intent.getBooleanExtra(IS_SUBMIT_SUCCESS, false)

                                if (isSuccessSubmitted) {
                                    Toasty.success(
                                        activity,
                                        activity.getString(R.string.answer_was_submitted_successfully)
                                    ).show()
                                    dialogBuilder.dismiss()
                                } else {
                                    Toasty.error(
                                        activity,
                                        activity.getString(R.string.no_internet_connection)
                                    ).show()
                                }

                                stopListeningSubmitAnswerResultBR()

                            }
                        }

                        startListeningSubmitAnswerResultBR()

                        submitBtn.startLoading()

                        val intent = Intent(DetailPurchaseFragment.SEND_ANSWER_ACTION).apply {
                            putExtra(DetailPurchaseFragment.STRING_ANSWER_EXTRA, answer)
                            putExtra(DetailPurchaseFragment.QUESTION_ID_EXTRA, potentialItem.questionId)
                        }

                        activity.applicationContext.sendBroadcast(intent)
                    }
                }

                val cancelBtn = dialogView.findViewById<Button>(R.id.cancel)
                cancelBtn.setOnClickListener {
                    dialogBuilder.dismiss()
                }
            }else{
                dialogView = layoutInflater.inflate(R.layout.boolean_question_alert_dialog, null)

                val questionTitle = dialogView.findViewById<TextView>(R.id.question_title)
                questionTitle.text = potentialItem.title

                val yesBtn:RadioButton = dialogView.findViewById(R.id.yes)
                val clickListener = View.OnClickListener { sendBooleanAnswer(yesBtn.isChecked, potentialItem.questionId) }
                yesBtn.setOnClickListener(clickListener)

                val noBtn:RadioButton = dialogView.findViewById(R.id.no)
                noBtn.setOnClickListener(clickListener)
            }

            dialogBuilder.setView(dialogView)
            dialogBuilder.show()
        }
    }

    private fun sendBooleanAnswer(checked: Boolean, questionId:Int) {
        val intent = Intent(DetailPurchaseFragment.SEND_ANSWER_ACTION).apply {
            putExtra(DetailPurchaseFragment.BOOLEAN_ANSWER_EXTRA, checked)
            putExtra(DetailPurchaseFragment.QUESTION_ID_EXTRA, questionId)
        }

        activity.applicationContext.sendBroadcast(intent)
    }

    override fun getItemCount(): Int {
        return potentialItemList.size
    }

    inner class PotentialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.potential_item_title)
        val isDone: ImageView = itemView.findViewById(R.id.potential_item_is_done)
    }

    fun startListeningSubmitAnswerResultBR() {
        mSubmitAnswerResponseBR?.let{
            activity.applicationContext.registerReceiver(it, IntentFilter(SUBMIT_ANSWER_ACTION))
        }
    }

    fun stopListeningSubmitAnswerResultBR(){
        try{
            activity.applicationContext.unregisterReceiver(mSubmitAnswerResponseBR!!)
        }catch (e:Exception){
            logException(e)
        }
    }


}