package com.example.stillpoint.ui.readerscreen

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
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
import com.example.stillpoint.utils.TtsManager
import com.example.stillpoint.utils.TtsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

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
    private val ttsManager: TtsManager,
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

    val ttsState : StateFlow<TtsState> = ttsManager.ttsState

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

    fun togglePlayback() {
        val currentArticle = _uiState.value.article ?: return

        when (ttsState.value) {
            TtsState.PLAYING -> ttsManager.stop()
            TtsState.IDLE -> {
                viewModelScope.launch {
                    val cleanText = withContext(Dispatchers.Default) {
                        Jsoup.parse(currentArticle.body ?: "").text()
                    }
                    ttsManager.speak(cleanText)
                }
            }
            TtsState.ERROR -> ttsManager.retryInitialization()
            else -> {/* Ignore INITIALIZING */}
        }
    }

    override fun onCleared() {
        super.onCleared()
        // IMPORTANT: Free up native TTS resources when the ViewModel is destroyed
        ttsManager.stop()
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
