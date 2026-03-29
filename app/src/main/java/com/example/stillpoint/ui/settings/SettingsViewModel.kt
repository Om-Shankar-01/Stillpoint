package com.example.stillpoint.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stillpoint.data.AppTheme
import com.example.stillpoint.data.FontType
import com.example.stillpoint.data.ReaderSettings
import com.example.stillpoint.data.ReaderTheme
import com.example.stillpoint.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userName: StateFlow<String> = userPreferencesRepository.userName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "User"
        )

    val appTheme: StateFlow<AppTheme> = userPreferencesRepository.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    val readerSettings: StateFlow<ReaderSettings> = userPreferencesRepository.readerSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReaderSettings(18, FontType.SANS_SERIF, ReaderTheme.SYSTEM)
        )

    private val _isEditNameDialogVisible = MutableStateFlow(false)
    val isEditNameDialogVisible: StateFlow<Boolean> = _isEditNameDialogVisible.asStateFlow()

    private val _isReaderSettingsDialogVisible = MutableStateFlow(false)
    val isReaderSettingsDialogVisible: StateFlow<Boolean> = _isReaderSettingsDialogVisible.asStateFlow()

    fun onShowEditNameDialog() {
        _isEditNameDialogVisible.value = true
    }

    fun onDismissEditNameDialog() {
        _isEditNameDialogVisible.value = false
    }

    fun onShowReaderSettingsDialog() {
        _isReaderSettingsDialogVisible.value = true
    }

    fun onDismissReaderSettingsDialog() {
        _isReaderSettingsDialogVisible.value = false
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserName(newName)
            onDismissEditNameDialog()
        }
    }

    fun updateAppTheme(theme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.updateAppTheme(theme)
        }
    }

    fun updateFontSize(size: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateFontSize(size)
        }
    }

    fun updateFontType(type: FontType) {
        viewModelScope.launch {
            userPreferencesRepository.updateFontType(type)
        }
    }

    fun updateReaderTheme(theme: ReaderTheme) {
        viewModelScope.launch {
            userPreferencesRepository.updateReaderTheme(theme)
        }
    }
}
