package com.example.stillpoint.ui

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stillpoint.data.ContentRepository
import com.example.stillpoint.data.local.ContentItem
import com.example.stillpoint.data.local.TimeFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(private val repository: ContentRepository) : ViewModel() {
    /* --- DIALOG STATE MANAGEMENT --- */
    private val _isAddDialogVisible = MutableStateFlow(false)
    val isAddDialogVisible: StateFlow<Boolean> = _isAddDialogVisible.asStateFlow()

    // A channel for sending one-time "side effects" to the UI, like showing a toast.
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onShowAddDialog() {
        _isAddDialogVisible.value = true
    }

    fun onDismissAddDialog() {
        _isAddDialogVisible.value = false
    }

    private val _selectedFilter = MutableStateFlow(TimeFilter.ALL)

    // A flow that gets all items from the database.
    private val _allItems = repository.getAllItems()

    // The final, public StateFlow for the UI. It combines the list of all items
    // and the selected filter, and emits a new, filtered list whenever either changes.
    val filteredItems: StateFlow<List<ContentItem>> =
        combine(_allItems, _selectedFilter) { items, filter ->
            when (filter) {
                TimeFilter.ALL -> items
                TimeFilter.FIVE_MIN -> items.filter { it.estimatedTimeMinutes <= 5 }
                TimeFilter.FIFTEEN_MIN -> items.filter { it.estimatedTimeMinutes in 6..15 }
                TimeFilter.THIRTY_MIN -> items.filter { it.estimatedTimeMinutes in 16..30 }
                TimeFilter.ONE_HOUR_PLUS -> items.filter { it.estimatedTimeMinutes > 30 }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val archivedItems: StateFlow<List<ContentItem>> = repository.getArchivedItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedFilter: StateFlow<TimeFilter> = _selectedFilter.asStateFlow()

    fun selectFilter(filter: TimeFilter) {
        _selectedFilter.value = filter
    }

    /* --- DELETE FUNCTIONS --- */
    fun deleteItem(item: ContentItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun deleteMultipleItems(items: List<ContentItem>) {
        viewModelScope.launch {
            repository.deleteMultipleItems(items)
        }
    }

    /* --- ARCHIVE FUNCTION --- */
    fun archiveItem(item: ContentItem) {
        viewModelScope.launch {
            repository.archiveItem(item)
        }
    }

    /* --- SAVE FUNCTION --- */
    fun saveManuallyAddedLink(url: String) {
        onDismissAddDialog()
        if (url.isBlank() || !url.startsWith("http")) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowToast("Please enter a valid URL"))
            }
            return
        }

        viewModelScope.launch {
            val result = repository.saveContentFromUrl(url)
            if (result.isSuccess) {
                _uiEvent.send(UiEvent.ShowToast("Saved to Stillpoint!"))
            } else {
                _uiEvent.send(UiEvent.ShowToast("Failed to save link."))
            }
        }
    }
}

// A sealed class to represent events we send to the UI
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}


