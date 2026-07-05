package com.example

import com.example.api.YTMusicApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FetchTest {
    @Test
    fun testFetch() = runBlocking {
        println("Fetching music...")
        val res = YTMusicApi.searchSongs("top hits pop 2024")
        println("Result: " + res.size)
        res.forEach { println(it.trackName + " - " + it.previewUrl) }
        
        if (res.isNotEmpty()) {
            val url = res[0].previewUrl
            if (url != null) {
                val stream = YTMusicApi.getStreamUrl(url)
                println("Stream URL: " + stream)
            }
        }
    }
}
