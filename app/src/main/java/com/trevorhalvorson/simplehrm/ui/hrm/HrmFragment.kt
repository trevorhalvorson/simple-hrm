package com.trevorhalvorson.simplehrm.ui.hrm

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.trevorhalvorson.simplehrm.R
import com.trevorhalvorson.simplehrm.model.HeartRate
import com.trevorhalvorson.simplehrm.viewmodel.HrViewModel
import kotlin.math.roundToInt

@ExperimentalStdlibApi
class HrmFragment : Fragment() {

    companion object {
        val TAG = "HrmFragment"

        val CHART_DATA_SET_LABEL = "HR_DATA"
        val CHART_MAX_DATA_POINTS = 10
        val CHART_INITIAL_HR = 60F
        val CHART_MIN_OFFSET_RATIO = 0.8F
        val CHART_MAX_OFFSET_RATIO = 1.2F

        fun newInstance() = HrmFragment()
    }

    private lateinit var hrViewModel: HrViewModel
    private lateinit var hrTv: TextView
    private lateinit var hrChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.hrm_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hrTv = view.findViewById(R.id.hr_data_tv)
        hrChart = view.findViewById(R.id.hr_chart)

        hrViewModel = ViewModelProvider(requireActivity()).get(HrViewModel::class.java)
        hrViewModel.heartRate.observe(this, Observer { heartRate ->
            val bpm = heartRate.bpm
            hrTv.text = bpm.roundToInt().toString()
            updateChart(bpm)
        })

        setupChart()
    }

    private fun setupChart() {
        // Chart settings
        hrChart.description.isEnabled = false
        hrChart.isDragEnabled = false
        hrChart.isAutoScaleMinMaxEnabled = false
        hrChart.legend.isEnabled = false
        hrChart.xAxis.isEnabled = false
        hrChart.xAxis.axisMinimum = 0F
        hrChart.xAxis.axisMaximum = CHART_MAX_DATA_POINTS.toFloat()
        hrChart.axisLeft.isEnabled = false
        hrChart.axisRight.isEnabled = false
        hrChart.setTouchEnabled(false)
        hrChart.setScaleEnabled(false)
        hrChart.setPinchZoom(false)
        hrChart.setDrawGridBackground(false)
        hrChart.invalidate()

        // Initial Chart data
        val initialData = arrayListOf<Entry>()
        for (i in 0 until CHART_MAX_DATA_POINTS) {
            initialData.add(Entry(i * 1F, CHART_INITIAL_HR))
        }
        val dataSet = LineDataSet(initialData, CHART_DATA_SET_LABEL)

        // DataSet settings
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.3f
        dataSet.setDrawCircles(false)
        dataSet.lineWidth = 5f
        dataSet.color = ContextCompat.getColor(hrChart.context, R.color.red_500)
        dataSet.setDrawHorizontalHighlightIndicator(false)

        val data = LineData(dataSet)
        data.setDrawValues(false)

        hrChart.data = data
    }

    private fun updateChart(newValue: Float) {
        val dataSet = hrChart.data.getDataSetByLabel(CHART_DATA_SET_LABEL, true) as LineDataSet
        dataSet.values.removeFirstOrNull()
        for (i in dataSet.values.indices) {
            dataSet.values[i].x = i * 1F
        }
        dataSet.values.add(Entry(dataSet.values.size * 1F, newValue))

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
        hrChart.axisLeft.axisMinimum = minValue * CHART_MIN_OFFSET_RATIO
        hrChart.axisLeft.axisMaximum = maxValue * CHART_MAX_OFFSET_RATIO
        hrChart.data.notifyDataChanged()
        hrChart.notifyDataSetChanged()
        hrChart.invalidate()
    }

}
