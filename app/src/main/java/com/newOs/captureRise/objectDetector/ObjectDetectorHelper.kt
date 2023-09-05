package com.newOs.captureRise.objectDetector

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.newOs.captureRise.dataStore.DataStoreManager
import com.newOs.captureRise.managers.MyAlarmManager
import com.newOs.captureRise.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    var threshold: Float = 0.5f,
    var numThreads: Int = 2,
    var maxResults: Int = 3,
    var currentModel: Int = 0,
    val context: Context,
    val objectDetectorListener: DetectorListener
) {


    private val TAG = "ObjectDetectionHelper"

    // For this example this needs to be a var so it can be reset on changes. If the ObjectDetector will not change, a lazy val would be preferable.
    private var objectDetector: ObjectDetector? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable: Boolean ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) { optionsBuilder.setEnableGpuDelegateSupport(true) }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            objectDetectorListener.onInitialized()
        }.addOnFailureListener{
            objectDetectorListener.onError("TfLiteVision failed to initialize: " + it.message)
        }
    }

    fun clearObjectDetector() {
        objectDetector = null
    }

    // Initialize the object detector using current settings on the thread that is using it. CPU and NNAPI delegates can be used with detectors
    // that are created on the main thread and used on a background thread, but the GPU delegate needs to be used on the thread that initialized the detector
    fun setupObjectDetector() {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG, "setupObjectDetector: TfLiteVision is not initialized yet")
            return
        }

        // Create the base options for the detector using specifies max results and score threshold
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder().setScoreThreshold(threshold).setMaxResults(maxResults)

        // Set general detection options, including number of used threads
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        val modelName =
            when (currentModel) {
                MODEL_MOBILENETV1 -> "mobilenetv1.tflite"
                MODEL_EFFICIENTDETV0 -> "efficientdet-lite0.tflite"
                MODEL_EFFICIENTDETV1 -> "efficientdet-lite1.tflite"
                MODEL_EFFICIENTDETV2 -> "efficientdet-lite2.tflite"
                else -> "mobilenetv1.tflite"
            }

        try {
            objectDetector = ObjectDetector.createFromFileAndOptions(context, modelName, optionsBuilder.build())
        } catch (e: Exception) {
            objectDetectorListener.onError("Object detector failed to initialize. See error logs for details")
            Log.e(TAG, "TFLite failed to load model with error: " + e.message)
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG, "detect: TfLiteVision is not initialized yet")
            return
        }

        if (objectDetector == null) { setupObjectDetector() }

        var inferenceTime = SystemClock.uptimeMillis()                  // Inference time is the difference between the system time at the start and finish of the process

        val imageProcessor = ImageProcessor.Builder().add(Rot90Op(-imageRotation / 90)).build()             // Create preprocessor for the image.

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))                     // Preprocess the image and convert it into a TensorImage for detection.

        val results = objectDetector?.detect(tensorImage)
        val dataStoreManager = DataStoreManager.getInstance(context)

        if (results != null && results.isNotEmpty()) {
            val detectedObjectNames = mutableListOf<String>()

            for (result in results) getDetectedObjectNames(result, detectedObjectNames)                 // Extract object labels from detection results

            val detectedObjectNamesString = detectedObjectNames.joinToString(", ")                          // Show a toast with the detected object names

            GlobalScope.launch {
                dataStoreManager.desiredObject.collect { desiredObj ->
                    if(ImageUtils.isStringExists(detectedObjectNames,desiredObj)){
                        MyAlarmManager.initialize(context)
                        MyAlarmManager.stopAlarm()
                        CoroutineScope(Dispatchers.IO).launch { dataStoreManager.setIsAlarmOn(false) }
                    }
                }
            }

            Log.d("OsOs", "Detected Objects: $detectedObjectNamesString")
        } else {
            Log.d("OsOs", "No objects detected")
        }


        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        objectDetectorListener.onResults(results, inferenceTime, tensorImage.height, tensorImage.width)
    }

    private fun getDetectedObjectNames(
        result: Detection,
        detectedObjectNames: MutableList<String>
    ) {
        for (category in result.categories) {
            detectedObjectNames.add(category.label)
        }
    }

    interface DetectorListener {
        fun onInitialized()
        fun onError(error: String)
        fun onResults(
          results: MutableList<Detection>?,
          inferenceTime: Long,
          imageHeight: Int,
          imageWidth: Int
        )
    }

    companion object {
        const val MODEL_MOBILENETV1 = 0
        const val MODEL_EFFICIENTDETV0 = 1
        const val MODEL_EFFICIENTDETV1 = 2
        const val MODEL_EFFICIENTDETV2 = 3
    }

}
