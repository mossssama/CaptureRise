package com.newOs.captureRise.utils

import java.util.*

class ImageUtils {

    companion object{

        val myStringList = listOf(
            "chair",
            "person",
            "potted plant",
            "keyboard",
            "laptop",
            "bottle",
            "bed",
            "couch",
            "suitcase",
            "vase",
            "microwave",
            "tv",
            "handbag",
            "refrigerator",
            "cup",
            "book",
            "backpack"
        )

        fun generateRandomNumberExc(upperBoundExclusive:Int): Int = Random().nextInt(upperBoundExclusive-1)

        fun isStringExists(stringList: List<String>, desired: String): Boolean = desired in stringList

    }
}