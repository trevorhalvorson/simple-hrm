package com.trevorhalvorson.simplehrm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trevorhalvorson.simplehrm.model.HeartRate

class HrViewModel : ViewModel() {
    val heartRate: MutableLiveData<HeartRate> by lazy {
        MutableLiveData<HeartRate>()
    }

    fun submitHr(hr: Float) {
        heartRate.value = HeartRate(hr)
    }
}
