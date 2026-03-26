package com.example.stillpoint.ui

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}
