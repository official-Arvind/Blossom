package com.example.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.downloader.CancellableCall
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SimpleDownloader : Downloader() {
    private val client = OkHttpClient.Builder().build()

    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        val requestBuilder = okhttp3.Request.Builder()
            .method(httpMethod, dataToSend?.toRequestBody())
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")

        headers.forEach { (headerName, headerValueList) ->
            if (headerValueList.size > 1) {
                requestBuilder.removeHeader(headerName)
                headerValueList.forEach { headerValue ->
                    requestBuilder.addHeader(headerName, headerValue)
                }
            } else if (headerValueList.size == 1) {
                requestBuilder.header(headerName, headerValueList[0])
            }
        }

        val response = client.newCall(requestBuilder.build()).execute()
        val latestUrl = response.request.url.toString()
        val responseBodyToReturn = response.body?.string()

        return Response(
            response.code,
            response.message,
            response.headers.toMultimap(),
            responseBodyToReturn,
            responseBodyToReturn?.toByteArray(),
            latestUrl
        )
    }

    override fun executeAsync(request: Request, callback: Downloader.AsyncCallback?): CancellableCall {
        throw UnsupportedOperationException("executeAsync is not implemented")
    }
}

object YTMusicApi {
    private var isInitialized = false

    fun init() {
        if (!isInitialized) {
            NewPipe.init(SimpleDownloader())
            isInitialized = true
        }
    }

    suspend fun searchSongs(query: String): List<ITunesTrack> = withContext(Dispatchers.IO) {
        try {
            init()
            val ytService = ServiceList.YouTube
            val searchExtractor = ytService.getSearchExtractor(query)
            searchExtractor.fetchPage()
            val initialPage = searchExtractor.initialPage
            val items = initialPage.items
            
            items.filterIsInstance<StreamInfoItem>()
                .filter { it.streamType == StreamType.AUDIO_STREAM || it.streamType == StreamType.VIDEO_STREAM }
                .take(15)
                .map { item ->
                    ITunesTrack(
                        trackId = item.url.hashCode().toLong(),
                        trackName = item.name,
                        artistName = item.uploaderName,
                        collectionName = "YouTube",
                        artworkUrl100 = item.thumbnailUrl,
                        previewUrl = item.url // We will use this URL to fetch the actual stream later
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getStreamUrl(videoUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            init()
            val ytService = ServiceList.YouTube
            val streamExtractor = ytService.getStreamExtractor(videoUrl)
            streamExtractor.fetchPage()
            val audioStreams = streamExtractor.audioStreams
            val bestAudio = audioStreams.maxByOrNull { it.averageBitrate }
            bestAudio?.content
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

suspend fun fetchRealMusic(query: String): List<ITunesTrack> {
    return YTMusicApi.searchSongs(query)
}
