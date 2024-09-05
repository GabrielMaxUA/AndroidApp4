package com.trios2024aa.itunes

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import androidx.media3.exoplayer.ExoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    repository: ITunesRepository = ITunesRepository(itunesService),
    viewModel: ITunesViewModel = viewModel(factory = ITunesViewModelFactory(repository))
) {
    val items by viewModel.items
    val searched by viewModel.searched
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var currentlyPlayingUrl by remember { mutableStateOf<String?>(null) }

    // Ensure MediaPlayer is properly disposed when the Composable is destroyed
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            query = query,
            onQueryChange = {
                // Exclude symbols: Only letters and numbers are allowed
                query = it
                    //.filter { char -> char.isLetterOrDigit() || char.isWhitespace() || char.}
            },
            onSearch = {
                if (query.isNotBlank()) {
                    viewModel.searchTunes(query.trim()) // Use ViewModel's searchTunes method
                }
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            modifier = Modifier.fillMaxWidth()
        ){

        }

        if (isLoading) {
            // Show a loading indicator
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        } else if (errorMessage != null) {
            // Show error message
            Text(text = errorMessage ?: "", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                when {
                    !searched -> {
                        // Initial state before any search
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    items.isNotEmpty() -> {
                        items(items) { item ->
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Check for null or missing image URL
                                val imageUrl = item.artworkUrl60 ?: "https://via.placeholder.com/60" // Placeholder image if null

                                val painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .transformations(CircleCropTransformation())
                                        .build()
                                )

                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(70.dp)
                                        .padding(end = 8.dp)
                                )

                                // Check for null or missing preview URL
                                val isCurrentTrackPlaying = currentlyPlayingUrl == item.previewUrl
                                Icon(
                                    imageVector = if (isCurrentTrackPlaying) Icons.Default.Refresh else Icons.Default.PlayArrow,
                                    contentDescription = if (isCurrentTrackPlaying) "Pause Preview" else "Play Preview",
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(30.dp)
                                        .padding(end = 8.dp)
                                        .clickable {
                                            if (isCurrentTrackPlaying) {
                                                mediaPlayer?.stop()
                                                mediaPlayer?.reset()
                                                currentlyPlayingUrl = null
                                            } else {
                                                val previewUrl = item.previewUrl
                                                if (!previewUrl.isNullOrEmpty()) { // Ensure previewUrl is not null or empty
                                                    // Play audio using MediaPlayer
                                                    try {
                                                        mediaPlayer?.stop()
                                                        mediaPlayer?.reset()
                                                        mediaPlayer = MediaPlayer().apply {
                                                            setDataSource(previewUrl)
                                                            prepare()
                                                            start()
                                                        }
                                                        currentlyPlayingUrl = previewUrl
                                                    } catch (e: Exception) {
                                                        Log.e("CategoryItem", "Error playing audio: ${e.message}")
                                                    }
                                                }
                                            }
                                        }
                                )

                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.artistName ?: "Unknown Artist", // Fallback for null artist name
                                            style = TextStyle(fontWeight = FontWeight.Bold),
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .fillMaxWidth(0.5f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.kind ?: "Unknown", // Fallback for null kind
                                            style = TextStyle(fontWeight = FontWeight.Light),
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .padding(end = 30.dp)
                                        )
                                    }

                                    Text(
                                        text = item.trackName ?: "Unknown Track", // Fallback for null track name
                                        style = TextStyle(fontWeight = FontWeight.Light),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        item {
                            Text(
                                text = "No results found",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



