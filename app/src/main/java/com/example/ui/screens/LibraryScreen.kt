package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.bouncyClickable

import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ui.viewmodels.MusicPlayerViewModel

@Composable
fun LibraryScreen(
    playerViewModel: MusicPlayerViewModel,
    viewModel: SearchViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        if (viewModel.searchResults.isEmpty()) {
            viewModel.searchQuery = "chill vibes"
            viewModel.search()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Your Library", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))

        val context = androidx.compose.ui.platform.LocalContext.current
        
        LibraryItem(Icons.AutoMirrored.Filled.PlaylistPlay, "Playlists", "Create playlists") {
            android.widget.Toast.makeText(context, "Playlists feature coming to Blossom Premium", android.widget.Toast.LENGTH_SHORT).show()
        }
        Spacer(modifier = Modifier.height(16.dp))
        LibraryItem(Icons.Filled.History, "Recent", "View recent tracks") {
            android.widget.Toast.makeText(context, "Recent history is synced", android.widget.Toast.LENGTH_SHORT).show()
        }
        Spacer(modifier = Modifier.height(16.dp))
        LibraryItem(Icons.Filled.Favorite, "Liked Songs", "Save your favorites") {
            android.widget.Toast.makeText(context, "Syncing your favorites...", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("Recommended for You", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(viewModel.searchResults.take(10).size) { index ->
                    val track = viewModel.searchResults[index]
                    Row(
                        modifier = Modifier.fillMaxWidth().bouncyClickable {
                            playerViewModel.playTrack(track)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = track.artworkUrl100,
                            contentDescription = "Artwork",
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(track.trackName ?: "Unknown Track", color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(track.artistName ?: "Unknown Artist", color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFF2C2C2E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}
