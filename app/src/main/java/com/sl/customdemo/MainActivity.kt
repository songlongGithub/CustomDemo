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
//        weightView.setWeight(50f,40f,60f,"偏高",1,20f);
        val list = (0..100).toList()
        val mutableList = mutableListOf<DataBean>()
        for (i in list) {
            mutableList.add(
                DataBean(
                    "2019-10-10",
                    Random.nextInt(100) + 0.5
                )
            )
        }

        day_trend1.setData(mutableList, "kg")
    }

}
