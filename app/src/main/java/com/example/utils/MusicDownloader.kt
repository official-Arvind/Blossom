package com.example.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

object MusicDownloader {
    private val client = OkHttpClient()

    suspend fun downloadTrack(context: Context, trackUrl: String, trackName: String, artistName: String) = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(trackUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                }
                return@withContext
            }

            val appDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Blossom")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }

            val safeTrackName = trackName.replace(Regex("[^a-zA-Z0-9.-]"), "_")
            val safeArtistName = artistName.replace(Regex("[^a-zA-Z0-9.-]"), "_")
            val fileName = "${safeArtistName}_${safeTrackName}.m4a"
            val file = File(appDir, fileName)

            val inputStream = response.body?.byteStream()
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Downloaded to Music/Blossom", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
