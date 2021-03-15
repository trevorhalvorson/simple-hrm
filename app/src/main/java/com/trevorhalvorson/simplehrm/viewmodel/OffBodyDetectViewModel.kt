package com.trevorhalvorson.simplehrm.viewmodel

import android.hardware.SensorEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OffBodyDetectViewModel : ViewModel() {
    val offBodyDetect: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun submitOffBodyDetectEvent(event: SensorEvent) {
        val value = event.values.firstOrNull()
        value?.let {
            offBodyDetect.value = it.equals(0.0F)
        }
    }
}
