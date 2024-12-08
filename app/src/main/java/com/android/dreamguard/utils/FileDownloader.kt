package com.android.dreamguard.utils

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileDownloader {
    fun downloadFile(context: Context, url: String, fileName: String): File {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) throw Exception("Failed to download file: ${response.code}")

        val file = File(context.filesDir, fileName)
        val inputStream: InputStream = response.body!!.byteStream()
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}
