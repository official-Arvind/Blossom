package com.example.api
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ITunesTrack(
    val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val artworkUrl100: String?,
    val previewUrl: String?
)
