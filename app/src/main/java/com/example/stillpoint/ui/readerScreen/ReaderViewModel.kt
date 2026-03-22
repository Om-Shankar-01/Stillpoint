package com.example.stillpoint.ui.readerScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.stillpoint.data.ArticleContent
import com.example.stillpoint.data.ContentRepository
import com.example.stillpoint.data.FontType
import com.example.stillpoint.data.ReaderSettings
import com.example.stillpoint.data.ReaderTheme
import com.example.stillpoint.data.UserPreferencesRepository
import com.example.stillpoint.ui.Reader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

data class ReaderUiState (
    val isLoading : Boolean = true,
    val article : ArticleContent? = null,
    val error: String? = null,
    val isSettingsVisible: Boolean = false,
    val url: String = ""
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState = _uiState.asStateFlow()

    val settings: StateFlow<ReaderSettings> = userPreferencesRepository.readerSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReaderSettings(18, FontType.SANS_SERIF, ReaderTheme.SYSTEM)
        )

    init {
        // Retrieve the URL argument passed via navigation.
        val readerRoute: Reader = savedStateHandle.toRoute()
        val url = readerRoute.url

        if (url.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(url = url)
            loadArticleContent(url)
        } else {
            _uiState.value = ReaderUiState(isLoading = false, error = "Article URL not found.")
        }
    }

    fun toggleSettings() {
        _uiState.value = _uiState.value.copy(isSettingsVisible = !_uiState.value.isSettingsVisible)
    }

    fun updateFontSize(newSize: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateFontSize(newSize)
        }
    }

    fun updateFontType(newType: FontType) {
        viewModelScope.launch {
            userPreferencesRepository.updateFontType(newType)
        }
    }

    fun updateTheme(newTheme: ReaderTheme) {
        viewModelScope.launch {
            userPreferencesRepository.updateReaderTheme(newTheme)
        }
    }

    fun openInBrowser(context: Context) {
        val url = _uiState.value.url
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        }
    }

    private fun loadArticleContent(url: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, url = url)
            val result = repository.getArticleContent(url)
            result.onSuccess { articleContent ->
                _uiState.value = _uiState.value.copy(isLoading =  false, article = articleContent)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = exception.message)
            }
        }
    }
}
