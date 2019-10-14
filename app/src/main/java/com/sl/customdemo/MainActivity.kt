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

        weightView.setWeight(80f, 90f, 100f, "偏高20kg");
    }

}
