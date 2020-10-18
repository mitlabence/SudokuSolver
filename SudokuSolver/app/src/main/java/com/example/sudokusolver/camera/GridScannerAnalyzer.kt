package com.example.sudokusolver.camera

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.text.TextRecognition


class GridScannerAnalyzer : ImageAnalysis.Analyzer {

    override fun analyze(proxy: ImageProxy) {

        val mediaImage =  proxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
            val recognizer = TextRecognition.getClient()
            Log.d(TAG, "Attempting to process.")
            val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                    // ...
                    Log.d(TAG, "Analyzing successful.")
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    Log.d(TAG, "Analyze unsuccessful!!!")
                }.addOnCompleteListener { results -> kotlin.run{
                    proxy.close()
                    Log.d(TAG, "successfully processed.")
                    val resultText = results.result
                    for (block in resultText!!.textBlocks) {
                        val blockText = block.text
                        val blockCornerPoints = block.cornerPoints
                        val blockFrame = block.boundingBox
                        for (line in block.lines) {
                            val lineText = line.text
                            val lineCornerPoints = line.cornerPoints
                            val lineFrame = line.boundingBox
                            for (element in line.elements) {
                                val elementText = element.text
                                Log.d(TAG, elementText)
                                val elementCornerPoints = element.cornerPoints
                                val elementFrame = element.boundingBox
                                //TODO text recognition now seems to be working. The problem is that blocks (sudoku cells) are not found! Need edge detection and some more complicated pre-processing.
                            }
                        }
                    }
                } }

        }
    }
    companion object {
        val TAG = "GridScannerAnalyzer"
    }
}