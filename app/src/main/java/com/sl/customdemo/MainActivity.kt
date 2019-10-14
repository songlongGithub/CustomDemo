package com.sl.customdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sl.customdemo.chart.DataBean
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        trendCurveView()

        dialView()
    }


    /**
     * 贝塞尔曲线
     */
    private fun trendCurveView() {
        val list = (0..1000).toList()
        val mutableList = mutableListOf<DataBean>()
        for (i in list) {
            mutableList.add(
                DataBean(
                    "2019-10-10",
                    Random.nextInt(100) + 0.5
                )
            )
        }
        trendCurveView.setData(mutableList, "kg")
    }

    /**
     * 体重表盘
     */
    private fun dialView() {
        var weight = 80f
        weightView.setWeight(weight, 90f, 120f)
        addValue.setOnClickListener {
            weight += 10f
            weightView.setWeight(weight, 90f, 120f)
        }
        subtractionValue.setOnClickListener {
            weight -= 10f
            if (weight < 0) {
                weight = 0f
            }
            weightView.setWeight(weight, 90f, 120f)
        }
    }

}
