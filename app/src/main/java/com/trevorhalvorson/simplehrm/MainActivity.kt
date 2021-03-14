package com.trevorhalvorson.simplehrm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trevorhalvorson.simplehrm.ui.hrm.HrmFragment

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HrmFragment.newInstance())
                .commitNow()
        }
    }
}
