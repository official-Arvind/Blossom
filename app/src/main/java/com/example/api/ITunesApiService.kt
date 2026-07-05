package com.example.api

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class ITunesResponse(
    val resultCount: Int,
    val results: List<ITunesTrack>
)

@JsonClass(generateAdapter = true)
data class ITunesTrack(
    val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val artworkUrl100: String?,
    val previewUrl: String?
)

interface ITunesApiService {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 10
    ): ITunesResponse
}

object ITunesRetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: ITunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ITunesApiService::class.java)
    }
}

suspend fun fetchRealMusic(query: String): List<ITunesTrack> = withContext(Dispatchers.IO) {
    try {
        val response = ITunesRetrofitClient.service.searchTracks(query)
        response.results
    } catch (e: Exception) {
        emptyList()
    }
}
