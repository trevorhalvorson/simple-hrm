package com.trevorhalvorson.simplehrm.ui.hrm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.trevorhalvorson.simplehrm.R

private const val ARG_LABEL = "label"

class LoadingFragment : Fragment() {

    companion object {
        val TAG = "LoadingFragment"

        fun newInstance(label: String) =
            LoadingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LABEL, label)
                }
            }
    }

    private lateinit var labelTv: TextView
    private var label: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            label = it.getString(ARG_LABEL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.loading_fragment, container, false)

        labelTv = view.findViewById(R.id.label_tv)
        labelTv.text = label

        return view
    }

}
