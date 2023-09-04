package com.newOs.captureRise.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.newOs.captureRise.R
import com.newOs.captureRise.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {

    private lateinit var activityCameraBinding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) { finishAfterTransition() }
        else { super.onBackPressed() }
    }

}