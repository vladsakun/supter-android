package com.supter.ui.main.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.allyants.boardview.BoardAdapter
import com.supter.R

class DashboardAdapter(
    context: Context, val dataList: ArrayList<String> = ArrayList()
) : BoardAdapter(context) {


    override fun getColumnCount(): Int {
        return 4
    }

    override fun getItemCount(p0: Int): Int {
        return dataList.size
    }

    override fun createHeaderObject(p0: Int): Any {
        return "null"
    }

    override fun createFooterObject(p0: Int): Any {
        return "null"
    }

    override fun createItemObject(p0: Int, p1: Int): Any {
        return "null"
    }

    override fun isColumnLocked(p0: Int): Boolean {
        return false
    }

    override fun isItemLocked(p0: Int): Boolean {
        return false
    }

    override fun createItemView(
        context: Context,
        p1: Any?,
        p2: Any?,
        p3: Int,
        position: Int
    ): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val view = inflater.inflate(R.layout.purchase_item, null)
        val textView = view.findViewById<TextView>(R.id.purchase_name)
        textView.text = dataList[position]
        return view
    }

    override fun createHeaderView(context: Context, p1: Any?, position: Int): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val view = inflater.inflate(R.layout.purchase_item, null)
        val textView = view.findViewById<TextView>(R.id.purchase_name)
        textView.text = dataList[position]
        return view
    }

    override fun createFooterView(context: Context, p1: Any?, position: Int): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val view = inflater.inflate(R.layout.purchase_item, null)
        val textView = view.findViewById<TextView>(R.id.purchase_name)
        textView.text = dataList[position]
        return view    }
}