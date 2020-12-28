package wooyun.esnb.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_setting.*
import wooyun.esnb.R
import wooyun.esnb.base.BaseFragment
import wooyun.esnb.util.Tools


class SettingFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_setting
    }

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = Tools().spGet(requireActivity(), "wooyun.notepad_preferences")
        switch_quitAutomaticallyClearItsCacheFiles.isChecked = sharedPreferences.getBoolean("del_status", false)
        switch_quitAutomaticallyClearItsCacheFiles.setOnCheckedChangeListener { compoundButton, b ->
            Tools().spGet(requireActivity(), "wooyun.notepad_preferences").edit().putBoolean("del_status", b).apply()
        }
    }
}