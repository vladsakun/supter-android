package com.supter.ui.main.purchase.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.supter.R

class DetailPurchaseFragment : Fragment() {

    companion object {
        fun newInstance() = DetailPurchaseFragment()
    }

    private lateinit var viewModel: DetailPurchaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.detail_purchase_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailPurchaseViewModel::class.java)
        // TODO: Use the ViewModel
    }

}