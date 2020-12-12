package com.supter.ui.views

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.supter.R
import com.woxthebox.draglistview.DragItem

class MyDragItem(val context: Context, layoutId: Int) :
    DragItem(context, layoutId) {

    override fun onBindDragView(clickedView: View, dragView: View) {
        val name = (clickedView.findViewById<View>(R.id.purchase_title) as TextView).text
        val cost = (clickedView.findViewById<View>(R.id.purchase_cost) as TextView).text
        (dragView.findViewById<View>(R.id.purchase_title) as TextView).text = name
        (dragView.findViewById<View>(R.id.purchase_cost) as TextView).text = cost
        val dragCard: CardView = dragView.findViewById(R.id.card)
        val clickedCard: CardView = clickedView.findViewById(R.id.card)
        dragCard.maxCardElevation = 40f
        dragCard.cardElevation = clickedCard.cardElevation

        // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
        dragCard.foreground =
            ContextCompat.getDrawable(context, R.drawable.card_view_drag_foreground)
    }

    override fun onMeasureDragView(clickedView: View, dragView: View) {
        val dragCard: CardView = dragView.findViewById(R.id.card)
        val clickedCard: CardView = clickedView.findViewById(R.id.card)
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
        val dragCard: CardView = dragView.findViewById(R.id.card)
        val anim =
            ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 40f)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = ANIMATION_DURATION.toLong()
        anim.start()
    }

    override fun onEndDragAnimation(dragView: View) {
        val dragCard: CardView = dragView.findViewById(R.id.card)
        val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 6f)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = ANIMATION_DURATION.toLong()
        anim.start()
    }
}