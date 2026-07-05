package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.api.ITunesTrack
import com.example.api.fetchRealMusic
import com.example.ui.theme.*
import com.example.ui.viewmodels.MusicPlayerViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.utils.MusicDownloader
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable

import com.example.ui.components.bouncyClickable

class HomeViewModel : ViewModel() {
    var popTracks by mutableStateOf<List<ITunesTrack>>(emptyList())
    var rockTracks by mutableStateOf<List<ITunesTrack>>(emptyList())
    var workoutTracks by mutableStateOf<List<ITunesTrack>>(emptyList())

    init {
        viewModelScope.launch {
            popTracks = fetchRealMusic("top hits pop 2024")
            rockTracks = fetchRealMusic("classic rock")
            workoutTracks = fetchRealMusic("workout music mix")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), playerViewModel: MusicPlayerViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(YouTubeRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Blossom", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }
            },
            actions = {
                Icon(Icons.Filled.Search, contentDescription = "Search", modifier = Modifier.padding(end = 16.dp), tint = MaterialTheme.colorScheme.onBackground)
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF34D399))))
                        .border(1.dp, BorderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("AJ", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item { FilterChips() }
            item { GeminiPromptCard() }
            if (viewModel.popTracks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CategorySection(
                        "Trending Pop", 
                        viewModel.popTracks.map { it.toMediaItem() },
                        onItemClick = { item -> playerViewModel.playTrack(item.previewUrl, item.title, item.subtitle ?: "Unknown", item.imageUrl) },
                        onItemLongClick = { item -> 
                            coroutineScope.launch {
                                MusicDownloader.downloadTrack(context, item.previewUrl, item.title, item.subtitle ?: "Unknown")
                            }
                        }
                    )
                }
            }
            if (viewModel.workoutTracks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CategorySection(
                        "Energizing Workout", 
                        viewModel.workoutTracks.map { it.toMediaItem() }, 
                        isCircle = true,
                        onItemClick = { item -> playerViewModel.playTrack(item.previewUrl, item.title, item.subtitle ?: "Unknown", item.imageUrl) },
                        onItemLongClick = { item -> 
                            coroutineScope.launch {
                                MusicDownloader.downloadTrack(context, item.previewUrl, item.title, item.subtitle ?: "Unknown")
                            }
                        }
                    )
                }
            }
            if (viewModel.rockTracks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CategorySection(
                        "Classic Rock Mix", 
                        viewModel.rockTracks.map { it.toMediaItem() },
                        onItemClick = { item -> playerViewModel.playTrack(item.previewUrl, item.title, item.subtitle ?: "Unknown", item.imageUrl) },
                        onItemLongClick = { item -> 
                            coroutineScope.launch {
                                MusicDownloader.downloadTrack(context, item.previewUrl, item.title, item.subtitle ?: "Unknown")
                            }
                        }
                    )
                }
            }
        }
    }
}

fun ITunesTrack.toMediaItem() = MediaItem(
    title = this.trackName ?: "Unknown",
    subtitle = this.artistName ?: "Unknown Artist",
    imageUrl = this.artworkUrl100 ?: "",
    previewUrl = this.previewUrl ?: ""
)

@Composable
fun FilterChips() {
    val chips = listOf("Energize", "Workout", "Relax", "Commute", "Focus")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(chips.size) { index ->
            val isSelected = index == 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color.White else BorderColor)
                    .border(1.dp, if (isSelected) Color.Transparent else BorderColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = chips[index],
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = if (isSelected) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
fun GeminiPromptCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1a1a2e), Color(0xFF0a0a0a))))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(GeminiGradStart, GeminiGradMid, GeminiGradEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = "Gemini",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Ask Gemini", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text("\"I'm feeling a bit nostalgic for the early 2000s indie rock scene...\"", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BorderColor)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("Tap to talk about your mood...", style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic), color = TextSecondary)
            }
        }
    }
}

@Composable
fun CategorySection(
    title: String, 
    items: List<MediaItem>, 
    isCircle: Boolean = false,
    onItemClick: (MediaItem) -> Unit,
    onItemLongClick: (MediaItem) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                MediaCard(item, isCircle, { onItemClick(item) }, { onItemLongClick(item) })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaCard(item: MediaItem, isCircle: Boolean, onClick: () -> Unit, onLongClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(if (isCircle) 120.dp else 160.dp)
            .clip(RoundedCornerShape(8.dp))
            .bouncyClickable(onClick = onClick),
        horizontalAlignment = if (isCircle) Alignment.CenterHorizontally else Alignment.Start
    ) {
        AsyncImage(
            model = item.imageUrl.replace("100x100bb", "500x500bb"), // get better quality
            contentDescription = item.title,
            modifier = Modifier
                .size(if (isCircle) 120.dp else 160.dp)
                .clip(if (isCircle) CircleShape else RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
        if (item.subtitle != null) {
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

data class MediaItem(val title: String, val subtitle: String?, val imageUrl: String, val previewUrl: String)
