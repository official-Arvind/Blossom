package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GeminiFab
import com.example.ui.components.MusicPlayerBottomBar
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.viewmodels.MusicPlayerViewModel
import com.example.ui.components.bouncyClickable

enum class Screen(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Search("Search", Icons.Filled.Search),
    Explore("Explore", Icons.Filled.Explore),
    Library("Library", Icons.Filled.LibraryMusic)
}

@Composable
fun MainScreen(playerViewModel: MusicPlayerViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    var globalSearchQuery by remember { mutableStateOf("") }
    var showProfileSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.background(ImmersiveBackground)) {
                MusicPlayerBottomBar(playerViewModel)
                NavigationBar(
                    containerColor = ImmersiveBackground,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    Screen.values().forEach { screen ->
                        NavigationBarItem(
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen },
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.bouncyClickable { currentScreen = screen }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            GeminiFab()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ImmersiveBackground)
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    playerViewModel = playerViewModel,
                    onNavigateToSearch = { currentScreen = Screen.Search },
                    onNavigateToProfile = { showProfileSheet = true }
                )
                Screen.Search -> SearchScreen(initialQuery = globalSearchQuery, playerViewModel = playerViewModel)
                Screen.Explore -> ExploreScreen(onGenreSelected = { genre -> 
                    globalSearchQuery = genre
                    currentScreen = Screen.Search
                })
                Screen.Library -> LibraryScreen(playerViewModel = playerViewModel)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF10B981))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "PREMIUM • OFFLINE",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }

    if (showProfileSheet) {
        ProfileBottomSheet(onDismiss = { showProfileSheet = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBottomSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val context = androidx.compose.ui.platform.LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF34D399))))
                    .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("AJ", color = Color.White, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Arvind Ji", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text("arvindrtxgaming@gmail.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    android.widget.Toast.makeText(context, "Already signed in as Arvind Ji", android.widget.Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Sign In", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    android.widget.Toast.makeText(context, "Settings synced with cloud", android.widget.Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Settings", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
