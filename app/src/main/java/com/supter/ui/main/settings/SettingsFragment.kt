package com.supter.ui.main.settings

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.supter.databinding.FragmentSettingsBinding
import com.supter.utils.SystemUtils


class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private var _binding: FragmentSettingsBinding? = null
    val mBinding get() = _binding!!

    private lateinit var galleryViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        val currentColorMode = SystemUtils.getColorMode(requireContext().applicationContext)
        if(currentColorMode == AppCompatDelegate.MODE_NIGHT_YES){
            mBinding.changeThemeSwitch.isChecked = true
        }

        mBinding.changeThemeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                SystemUtils.setColorMode(
                    requireContext().applicationContext,
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            } else {
                SystemUtils.setColorMode(
                    requireContext().applicationContext,
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }
}