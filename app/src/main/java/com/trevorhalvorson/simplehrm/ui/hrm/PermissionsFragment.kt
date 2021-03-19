package com.trevorhalvorson.simplehrm.ui.hrm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trevorhalvorson.simplehrm.R

class PermissionsFragment : Fragment() {

    companion object {
        val TAG = "PermissionsFragment"

        fun newInstance() = PermissionsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.permissions_fragment, container, false)
    }

}
