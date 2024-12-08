package com.android.dreamguard.utils

import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object PredictionModelManager {

    fun loadInterpreter(modelFile: File): Interpreter {
        if (!modelFile.exists()) {
            throw Exception("Model file not found at path: ${modelFile.path}")
        }
        Log.d("PredictionModelManager", "Loading model from file: ${modelFile.path}")
        val fileInputStream = modelFile.inputStream()
        val fileChannel = fileInputStream.channel
        val mappedByteBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, modelFile.length())
        fileInputStream.close()
        return Interpreter(mappedByteBuffer)
    }
}
