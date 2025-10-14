package com.example.stillpoint.ui

import kotlinx.serialization.Serializable

// The home screen needs no arguments. A data object is perfect.
@Serializable data object Queue

@Serializable data object Archive

// The reader screen needs the article's URL.
// The serialization library will automatically handle encoding/decoding.
@Serializable
data class Reader(val url: String)