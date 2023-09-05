package com.newOs.captureRise.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.newOs.captureRise.R
import com.newOs.captureRise.dataStore.DataStoreManager
import com.newOs.captureRise.databinding.ActivityCameraBinding
import kotlinx.coroutines.launch

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityCameraBinding: ActivityCameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        val dataStoreManager = DataStoreManager.getInstance(this)

//        lifecycleScope.launch {
//            dataStoreManager.isAlarmOn.collect { isAlarmOn ->
//                if(!isAlarmOn){ finish() }
//            }
//        }
//    }
    }
        override fun onBackPressed() {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                finishAfterTransition()
            } else {
                super.onBackPressed()
            }
        }
    }