package com.supter.ui.main.settings

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.supter.R
import com.supter.databinding.FragmentSettingsBinding
import com.supter.utils.SystemUtils


class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private var _binding: FragmentSettingsBinding? = null
    val mBinding get() = _binding!!

    private lateinit var galleryViewModel: GalleryViewModel

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