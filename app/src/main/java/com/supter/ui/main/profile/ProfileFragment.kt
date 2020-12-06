package com.supter.ui.main.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.supter.R
import com.supter.databinding.FragmentProfileBinding
import com.supter.ui.auth.LoginActivity
import com.supter.utils.SystemUtils
import es.dmoral.toasty.Toasty

class ProfileFragment : Fragment() {
    private val TAG = "ProfileFragment"

    private var _binding: FragmentProfileBinding? = null
    private val mBinding: FragmentProfileBinding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.incomeRemainder.editText?.setText("20000")
        mBinding.period.editText?.setText("30")
        mBinding.incomeRemainder.setEndIconOnClickListener {
            Toasty.info(requireContext(), "Income remainder").show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logout()
            16908332 -> findNavController().navigateUp()
        }
        return true
    }

    private fun logout() {
        SystemUtils.deleteToken(requireContext().applicationContext)
        requireContext().startActivity(LoginActivity.getStartIntent(requireContext()))
    }

}