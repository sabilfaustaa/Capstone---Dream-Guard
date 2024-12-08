package com.android.dreamguard.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ModelDownloader {

    suspend fun downloadModel(context: Context, modelUrl: String): File {
        return withContext(Dispatchers.IO) {
            Log.d("ModelDownloader", "Downloading model from: $modelUrl")
            val url = URL(modelUrl)
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Failed to download model: HTTP ${connection.responseCode} - ${connection.responseMessage}")
                }

                val modelFile = File(context.filesDir, "model.tflite")
                connection.inputStream.use { input ->
                    FileOutputStream(modelFile).use { output ->
                        input.copyTo(output)
                    }
                }
                Log.d("ModelDownloader", "Model downloaded successfully to: ${modelFile.path}")
                modelFile
            } catch (e: Exception) {
                Log.e("ModelDownloader", "Error downloading model: ${e.message}")
                throw e
            } finally {
                connection.disconnect()
            }
        }
    }
}
