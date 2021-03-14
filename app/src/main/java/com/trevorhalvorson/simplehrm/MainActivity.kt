package com.trevorhalvorson.simplehrm

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*
import kotlin.math.roundToInt

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity(), SensorEventListener {
    companion object {
        val TAG = "MainActivity"
        val DATA_SET_LABEL = "HR_DATA"
        val MAX_DATA_POINTS = 10
        val INITIAL_HR = 60F
    }

    private lateinit var hrTv: TextView
    private lateinit var hrChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hrTv = findViewById(R.id.hr_data_tv)
        hrChart = findViewById(R.id.hr_chart)

        setupChart()
        initChartData()
    }

    override fun onResume() {
        super.onResume()

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val hrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        val offBodySensor = sensorManager.getDefaultSensor(Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT)

        sensorManager.also {
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
        Log.d(TAG, "onSensorChanged: ${event?.sensor?.type} ${Arrays.toString(event?.values)}")
        when (event?.sensor?.type) {
            Sensor.TYPE_HEART_RATE -> {
                val value = event.values.firstOrNull()
                value?.let {
                    val hr = value.roundToInt()
                    hrTv.text = hr.toString()
                    updateChart(hr.toFloat())
                }
            }

            Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> {
                Toast.makeText(this, "Place on wrist!", Toast.LENGTH_SHORT).show()
            }

            else -> {
                Log.d(TAG, "Unhandled sensor event")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged: ${sensor?.type} $accuracy")
    }

    private fun setupChart() {
        hrChart.description.isEnabled = false
        hrChart.isDragEnabled = false
        hrChart.isAutoScaleMinMaxEnabled = false
        hrChart.legend.isEnabled = false
        hrChart.xAxis.isEnabled = false
        hrChart.xAxis.axisMinimum = 0F
        hrChart.xAxis.axisMaximum = MAX_DATA_POINTS.toFloat()
        hrChart.axisLeft.isEnabled = false
        hrChart.axisRight.isEnabled = false
        hrChart.setTouchEnabled(false)
        hrChart.setScaleEnabled(false)
        hrChart.setPinchZoom(false)
        hrChart.setDrawGridBackground(false)
        hrChart.invalidate()
    }

    private fun initChartData() {
        val initialData = arrayListOf<Entry>()
        for (i in 0 until MAX_DATA_POINTS) {
            initialData.add(Entry(i * 1F, INITIAL_HR))
        }
        val dataSet = LineDataSet(initialData, DATA_SET_LABEL)

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.3f
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 2f
        dataSet.color = Color.RED
        dataSet.setDrawHorizontalHighlightIndicator(false)

        val data = LineData(dataSet)
        data.setDrawValues(false)

        hrChart.data = data
    }

    private fun updateChart(newValue: Float) {
        val newData = arrayListOf<Entry>()
        val dataSet = hrChart.data.getDataSetByLabel(DATA_SET_LABEL, true) as LineDataSet
        val oldData = dataSet.values
        oldData.removeFirstOrNull()
        for (i in oldData.indices) {
            newData.add(Entry(i * 1F, oldData[i].y))
        }
        newData.add(Entry(newData.size * 1F, newValue))
        dataSet.values = newData

        var minValue = Float.MAX_VALUE
        var maxValue = Float.MIN_VALUE
        for (i in dataSet.values.indices) {
            val value = dataSet.values[i].y
            if (value > maxValue) {
                maxValue = value
            }
            if (value < minValue) {
                minValue = value
            }
        }

        hrChart.axisLeft.resetAxisMaximum()
        hrChart.axisLeft.resetAxisMinimum()
        hrChart.axisLeft.axisMinimum = minValue * 0.9F
        hrChart.axisLeft.axisMaximum = maxValue * 1.1F
        hrChart.data.notifyDataChanged()
        hrChart.notifyDataSetChanged()
        hrChart.invalidate()
    }

}