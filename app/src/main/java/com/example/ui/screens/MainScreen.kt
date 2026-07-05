package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
                Screen.Home -> HomeScreen(playerViewModel = playerViewModel)
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
}
