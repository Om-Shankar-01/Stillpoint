package com.example.stillpoint.ui.readerScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.stillpoint.data.ArticleContent
import com.example.stillpoint.data.ContentRepository
import com.example.stillpoint.ui.Reader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReaderUiState (
    val isLoading : Boolean = true,
    val article : ArticleContent? = null,
    val error: String? = null,
)

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repository: ContentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Retrieve the URL argument passed via navigation.
        val readerRoute: Reader = savedStateHandle.toRoute()
        val url = readerRoute.url

        if (url.isNotEmpty()) {
            loadArticleContent(url)
        } else {
            _uiState.value = ReaderUiState(isLoading = false, error = "Article URL not found.")
        }
    }

    private fun loadArticleContent(url: String) {
        viewModelScope.launch {
            _uiState.value = ReaderUiState(isLoading = true)
            val result = repository.getArticleContent(url)
            result.onSuccess { articleContent ->
                _uiState.value = ReaderUiState(isLoading =  false, article = articleContent)
            }.onFailure { exception ->
                _uiState.value = ReaderUiState(isLoading = false, error = exception.message)
            }
        }
    }
}