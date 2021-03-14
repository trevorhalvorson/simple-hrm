package com.trevorhalvorson.simplehrm.viewmodel

import android.hardware.SensorEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trevorhalvorson.simplehrm.model.HeartRate

class HrViewModel : ViewModel() {
    val heartRate: MutableLiveData<HeartRate> by lazy {
        MutableLiveData<HeartRate>()
    }

    fun submitHrEvent(event: SensorEvent) {
        val value = event.values.firstOrNull()
        value?.let {
            heartRate.value = HeartRate(value)
        }
    }
}
