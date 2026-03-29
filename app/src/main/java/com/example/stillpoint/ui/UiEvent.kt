package com.example.stillpoint.ui

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class OpenVideo(val url: String) : UiEvent()
    data class NavigateToReader(val url: String) : UiEvent()
}
