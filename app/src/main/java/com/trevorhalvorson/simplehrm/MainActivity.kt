package com.trevorhalvorson.simplehrm

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.trevorhalvorson.simplehrm.ui.hrm.HrmFragment
import com.trevorhalvorson.simplehrm.ui.hrm.LoadingFragment
import com.trevorhalvorson.simplehrm.viewmodel.HrViewModel
import com.trevorhalvorson.simplehrm.viewmodel.OffBodyDetectViewModel
import java.util.*

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        val TAG = "MainActivity"
    }

    private lateinit var hrViewModel: HrViewModel
    private lateinit var offBodyDetectViewModel: OffBodyDetectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    LoadingFragment.newInstance(resources.getString(R.string.reading_hr_alert))
                )
                .commitNow()
        }

        hrViewModel = ViewModelProvider(this).get(HrViewModel::class.java)
        hrViewModel.heartRate.observe(this, Observer { _ ->
            val fragment = supportFragmentManager.findFragmentByTag(HrmFragment.TAG)
            if (fragment == null) {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        HrmFragment.newInstance(),
                        HrmFragment.TAG
                    )
                    .commitNow()
            }
        })

        offBodyDetectViewModel = ViewModelProvider(this).get(OffBodyDetectViewModel::class.java)
        offBodyDetectViewModel.offBodyDetect.observe(this, Observer { offBody ->
            if (offBody) {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        LoadingFragment.newInstance(resources.getString(R.string.off_body_alert))
                    )
                    .commitNow()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        LoadingFragment.newInstance(resources.getString(R.string.reading_hr_alert))
                    )
                    .commitNow()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.also {
            val hrSensor = it.getDefaultSensor(Sensor.TYPE_HEART_RATE)
            val offBodySensor = it.getDefaultSensor(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT)

            it.registerListener(this, hrSensor, SensorManager.SENSOR_DELAY_FASTEST)
            it.registerListener(this, offBodySensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)

        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d(
            TAG,
            "onSensorChanged: ${event?.sensor?.type} ${Arrays.toString(event?.values)}"
        )
        when (event?.sensor?.type) {
            Sensor.TYPE_HEART_RATE -> {
                hrViewModel.submitHrEvent(event)
            }

            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> {
                offBodyDetectViewModel.submitOffBodyDetectEvent(event)
            }

            else -> {
                Log.d(TAG, "Unhandled sensor event")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged: ${sensor?.type} $accuracy")
    }
}
