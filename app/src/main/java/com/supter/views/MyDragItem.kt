package com.supter.views

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.google.android.material.card.MaterialCardView
import com.supter.R
import com.woxthebox.draglistview.DragItem
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class MyDragItem(val context: Context, layoutId: Int) :
    DragItem(context, layoutId) {

    private val TAG = "MyDragItem"

    override fun onBindDragView(clickedView: View, dragView: View) {

        val realPeriodView = clickedView.findViewById(R.id.real_period) as TextView
        val availabilityTimeFinishedView =
            clickedView.findViewById(R.id.availability_time_finished_emoji) as ImageView

        val name = (clickedView.findViewById(R.id.purchase_title) as TextView).text
        val cost = (clickedView.findViewById(R.id.purchase_cost) as TextView).text
        val realPeriod = (clickedView.findViewById(R.id.real_period) as TextView).text
        val potential = (clickedView.findViewById(R.id.potential) as ProgressBar).progress
        val purchaseDrawable =
            (clickedView.findViewById(R.id.purchase_image) as CircleImageView).drawable
        val availabilityPeriodFinished = availabilityTimeFinishedView.drawable

        if (realPeriodView.isVisible) {
            (dragView.findViewById(R.id.real_period) as TextView).text = realPeriod
        } else {
            availabilityTimeFinishedView.isVisible = true
            (dragView.findViewById<ImageView>(R.id.availability_time_finished_emoji)).setImageDrawable(
                availabilityPeriodFinished
            )
        }

        (dragView.findViewById(R.id.purchase_title) as TextView).text = name
        (dragView.findViewById(R.id.purchase_cost) as TextView).text = cost
        (dragView.findViewById(R.id.potential) as ProgressBar).progress = potential
        (dragView.findViewById<CircleImageView>(R.id.purchase_image)).setImageDrawable(
            purchaseDrawable
        )

        val dragCard: MaterialCardView = dragView.findViewById(R.id.card)
        val clickedCard: MaterialCardView = clickedView.findViewById(R.id.card)

        dragCard.maxCardElevation = 40f
        dragCard.cardElevation = clickedCard.cardElevation

//        // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
//        dragCard.foreground =
//            ContextCompat.getDrawable(context, R.drawable.card_view_drag_foreground)
    }

    override fun onMeasureDragView(clickedView: View, dragView: View) {

        val dragCard: MaterialCardView = dragView.findViewById(R.id.card)
        val clickedCard: MaterialCardView = clickedView.findViewById(R.id.card)

        val widthDiff = dragCard.paddingLeft - clickedCard.paddingLeft + dragCard.paddingRight -
                clickedCard.paddingRight
        val heightDiff = dragCard.paddingTop - clickedCard.paddingTop + dragCard.paddingBottom -
                clickedCard.paddingBottom

        val width = clickedView.measuredWidth + widthDiff
        val height = clickedView.measuredHeight + heightDiff
        dragView.layoutParams = FrameLayout.LayoutParams(width, height)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        dragView.measure(widthSpec, heightSpec)
    }

    override fun onStartDragAnimation(dragView: View) {
        val dragCard: MaterialCardView = dragView.findViewById(R.id.card)
        val anim =
            ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 40f)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = ANIMATION_DURATION.toLong()
        anim.start()
    }

    override fun onEndDragAnimation(dragView: View) {
        val dragCard: MaterialCardView = dragView.findViewById(R.id.card)
        val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 6f)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = ANIMATION_DURATION.toLong()
        anim.start()
    }
}