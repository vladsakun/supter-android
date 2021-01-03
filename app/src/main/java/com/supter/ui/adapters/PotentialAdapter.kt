package com.supter.ui.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.supter.R
import com.supter.data.PotentialItem
import com.supter.ui.main.purchase.detail.DetailPurchaseFragment
import es.dmoral.toasty.Toasty

class PotentialAdapter(
    val potentialItemList: MutableList<PotentialItem>,
    val isDone: Boolean,
    val activity: Activity,
    val purchaseId: Int,
) : RecyclerView.Adapter<PotentialAdapter.PotentialViewHolder>() {

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
            val dialogView = layoutInflater.inflate(R.layout.question_alert_dialog, null)

            val questionTitle = dialogView.findViewById<TextView>(R.id.question_title)
            questionTitle.text = potentialItem.title

            val answerEditText = dialogView.findViewById<TextInputLayout>(R.id.question_answer)

            val submitBtn = dialogView.findViewById<Button>(R.id.submit)
            submitBtn.setOnClickListener {

                val answer = answerEditText.editText?.text.toString().trim()

                if (answer.isBlank()) {
                    Toasty.warning(activity, activity.getString(R.string.answer_cant_be_blank))
                } else {

                    val intent = Intent(DetailPurchaseFragment.SEND_ANSWER_ACTION).apply {
                        putExtra(DetailPurchaseFragment.ANSWER_EXTRA, answer)
                        putExtra(DetailPurchaseFragment.QUESTION_ID_EXTRA, potentialItem.questionId)
                    }

                    activity.applicationContext.sendBroadcast(intent)
                }
            }

            val cancelBtn = dialogView.findViewById<Button>(R.id.cancel)
            cancelBtn.setOnClickListener {
                dialogBuilder.dismiss()
            }

            dialogBuilder.setView(dialogView)
            dialogBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return potentialItemList.size
    }

    inner class PotentialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.potential_item_title)
        val isDone: ImageView = itemView.findViewById(R.id.potential_item_is_done)
    }
}