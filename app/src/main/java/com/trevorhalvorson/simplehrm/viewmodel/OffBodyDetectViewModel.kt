package com.trevorhalvorson.simplehrm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OffBodyDetectViewModel : ViewModel() {
    val offBodyDetect: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun submitOffBodyDetect(isOffBody: Boolean) {
        offBodyDetect.value = isOffBody
    }
}
