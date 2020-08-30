package com.supter.ui.main.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.allyants.boardview.SimpleBoardAdapter
import com.allyants.boardview.SimpleBoardAdapter.SimpleColumn
import com.supter.R
import com.supter.databinding.FragmentDashboardBinding
import java.util.*

class DashboardFragment : Fragment() {

    lateinit var mBinding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        val boardView = mBinding.boardView
        val data = ArrayList<SimpleColumn>()
        val list = ArrayList<String>()
        list.add("Item 1")
        list.add("Item 2")
        list.add("Item 3")
        list.add("Item 4")
//        data.add(SimpleColumn("Column 1", list))
//        data.add(SimpleColumn("Column 2", list))
//        data.add(SimpleColumn("Column 3", list))
//        data.add(SimpleColumn("Column 4", list))
//        data.add(SimpleColumn("Column 5", list))
        val boardAdapter = DashboardAdapter(requireContext(), list)
        boardView.setAdapter(boardAdapter)
        return mBinding.root
    }
}