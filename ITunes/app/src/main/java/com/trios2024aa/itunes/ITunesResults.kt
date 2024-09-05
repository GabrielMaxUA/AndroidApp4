package com.trios2024aa.itunes


data class TuneItem(
    val kind: String,
    val artistName: String,
    val trackName: String,
    val artworkUrl60: String,
    val primaryGenreName: String,
    val previewUrl: String
)

data class ITunesResponse (
    val results: List<TuneItem>
)