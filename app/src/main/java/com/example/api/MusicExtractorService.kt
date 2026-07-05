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
data class SaavnSong(
    val id: String?,
    val title: String?,
    val subtitle: String?,
    val image: String?,
    val url: String?
)

interface MusicExtractorApi {
    @GET("search/songs")
    suspend fun searchSongs(
        @Query("query") query: String
    ): List<SaavnSong>
}

object MusicExtractorClient {
    private const val BASE_URL = "https://saavn-api.vercel.app/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: MusicExtractorApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MusicExtractorApi::class.java)
    }
}

suspend fun fetchRealMusic(query: String): List<ITunesTrack> = withContext(Dispatchers.IO) {
    try {
        val response = MusicExtractorClient.service.searchSongs(query)
        response.map {
            ITunesTrack(
                trackId = it.id?.hashCode()?.toLong() ?: 0L,
                trackName = it.title?.replace("&quot;", "\""),
                artistName = it.subtitle?.split("-")?.firstOrNull()?.trim(),
                collectionName = "Single",
                artworkUrl100 = it.image?.replace("50x50", "500x500")?.replace("150x150", "500x500"),
                previewUrl = it.url
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}
