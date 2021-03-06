package com.trevorhalvorson.simplehrm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.trevorhalvorson.simplehrm.ui.hrm.HrmFragment
import com.trevorhalvorson.simplehrm.ui.hrm.LoadingFragment
import com.trevorhalvorson.simplehrm.ui.hrm.PermissionsFragment
import com.trevorhalvorson.simplehrm.viewmodel.HrViewModel
import com.trevorhalvorson.simplehrm.viewmodel.OffBodyDetectViewModel
import java.util.*

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        val TAG = "MainActivity"
        val REQ_CODE_PERM_BODY_SENSORS = 1
    }

    private lateinit var hrViewModel: HrViewModel
    private lateinit var offBodyDetectViewModel: OffBodyDetectViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQ_CODE_PERM_BODY_SENSORS -> {
                if ((grantResults.isEmpty() ||
                            grantResults[0] != PackageManager.PERMISSION_GRANTED)
                ) {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container,
                            PermissionsFragment.newInstance()
                        )
                        .commitNow()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    LoadingFragment.newInstance(resources.getString(R.string.app_name))
                )
                .commitNow()
        }

        hrViewModel = ViewModelProvider(this).get(HrViewModel::class.java)
        hrViewModel.heartRate.observe(this, {
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
        offBodyDetectViewModel.offBodyDetect.observe(this, { offBody ->
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

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BODY_SENSORS),
                REQ_CODE_PERM_BODY_SENSORS
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            sensorManager.also {
                val hrSensor = it.getDefaultSensor(Sensor.TYPE_HEART_RATE)
                val offBodySensor = it.getDefaultSensor(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT)

                it.registerListener(this, hrSensor, SensorManager.SENSOR_DELAY_FASTEST)
                it.registerListener(this, offBodySensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onPause() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)

        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (BuildConfig.DEBUG) {
                Log.d(
                    TAG,
                    "onSensorChanged: " +
                            "type=${event.sensor?.type}, " +
                            "accuracy=${event.accuracy}, " +
                            "values=${Arrays.toString(event.values)}"
                )
            }

            if (event.accuracy >= SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
                when (event.sensor?.type) {
                    Sensor.TYPE_HEART_RATE -> {
                        val value = event.values.firstOrNull()
                        value?.let {
                            hrViewModel.submitHr(value)
                        }
                    }

                    Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> {
                        val value = event.values.firstOrNull()
                        value?.let {
                            offBodyDetectViewModel.submitOffBodyDetect(it.equals(0.0F))
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (BuildConfig.DEBUG) {
            Log.d(
                TAG, "onAccuracyChanged: " +
                        "type=${sensor?.type}, " +
                        "accuracy=$accuracy"
            )
        }
    }
}
