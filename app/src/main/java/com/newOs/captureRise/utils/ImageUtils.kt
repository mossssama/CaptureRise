package com.newOs.captureRise.utils

import java.util.*

class ImageUtils {

    companion object{

        val myStringList = listOf("chair", "person","potted plant","keyboard","laptop","bottle","bed","couch","suitcase","vase","microwave")

        // Generates a random number between 0 (inclusive) and 10 (exclusive)
        fun generateRandomNumberInc(upperBoundInclusive:Int): Int = Random().nextInt(upperBoundInclusive)
        fun generateRandomNumberExc(upperBoundExclusive:Int): Int = Random().nextInt(upperBoundExclusive-1)

        fun isStringExists(stringList: List<String>, desired: String): Boolean = desired in stringList

    }
}