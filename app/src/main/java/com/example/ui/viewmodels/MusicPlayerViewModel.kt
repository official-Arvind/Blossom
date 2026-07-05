package com.example.ui.viewmodels

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class MusicPlayerViewModel : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    var isPlaying by mutableStateOf(false)
    var currentTitle by mutableStateOf("No track selected")
    var currentArtist by mutableStateOf("")
    var currentArtUrl by mutableStateOf("")

    fun playTrack(track: com.example.api.ITunesTrack) {
        val url = track.previewUrl ?: return
        val title = track.trackName ?: "Unknown"
        val artist = track.artistName ?: "Unknown"
        val art = track.artworkUrl100 ?: ""
        playTrack(url, title, artist, art)
    }

    fun playTrack(url: String, title: String, artist: String, art: String) {
        currentTitle = title
        currentArtist = artist
        currentArtUrl = art
        isPlaying = false

        viewModelScope.launch(Dispatchers.IO) {
            val streamUrl = com.example.api.YTMusicApi.getStreamUrl(url)
            withContext(Dispatchers.Main) {
                if (streamUrl != null) {
                    playStream(streamUrl)
                }
            }
        }
    }

    private fun playStream(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    it.start()
                    this@MusicPlayerViewModel.isPlaying = true
                }
                setOnCompletionListener {
                    this@MusicPlayerViewModel.isPlaying = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPlaying = false
            } else {
                it.start()
                isPlaying = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
