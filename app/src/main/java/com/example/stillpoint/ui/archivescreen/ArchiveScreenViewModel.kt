package com.example.stillpoint.ui.archivescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stillpoint.data.ContentRepository
import com.example.stillpoint.data.local.ContentItem
import com.example.stillpoint.ui.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveScreenViewModel @Inject constructor(
    private val repository: ContentRepository
) : ViewModel() {

    // A channel for sending one-time "side effects" to the UI, like showing a toast.
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _isMultiDeleteDialogVisible = MutableStateFlow(false)
    val isMultiDeleteDialogVisible: StateFlow<Boolean> = _isMultiDeleteDialogVisible.asStateFlow()

    val archivedItems: StateFlow<List<ContentItem>> = repository.getArchivedItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onShowMultiDeleteDialog() {
        _isMultiDeleteDialogVisible.value = true
    }

    fun onDismissMultiDeleteDialog() {
        _isMultiDeleteDialogVisible.value = false
    }

    /* --- DELETE FUNCTIONS --- */
//    fun deleteMultipleItems(items: List<ContentItem>) {
//        viewModelScope.launch {
//            repository.deleteMultipleItems(items)
//            _uiEvent.send(UiEvent.ShowToast("Deleted ${items.size} items"))
//        }
//    }

    /* ---- SELECTION STATE ---- */
    private val _selectedItems = MutableStateFlow(setOf<ContentItem>())
    val selectedItems: StateFlow<Set<ContentItem>> = _selectedItems.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    fun toggleSelection(item: ContentItem) {
        val currentSelected = _selectedItems.value
        if (currentSelected.contains(item)) {
            val newSelected = currentSelected - item
            _selectedItems.value = newSelected
            if (newSelected.isEmpty()) {
                _isSelectionMode.value = false
            }
        } else {
            _selectedItems.value = currentSelected + item
            _isSelectionMode.value = true
        }
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
        _isSelectionMode.value = false
    }

    /* --- UNARCHIVE FUNCTION --- */
    fun unarchiveSelectedItems() {
        val itemsToUnarchive = _selectedItems.value.toList()
        viewModelScope.launch {
            repository.unarchiveMultipleItems(itemsToUnarchive)
            _uiEvent.send(UiEvent.ShowToast("Unarchived ${itemsToUnarchive.size} items"))
        }
    }

    fun deleteSelectedItems() {
        val itemsToDelete = _selectedItems.value.toList()
        viewModelScope.launch {
            repository.deleteMultipleItems(itemsToDelete)
            clearSelection()
            onDismissMultiDeleteDialog()
            _uiEvent.send(UiEvent.ShowToast("Deleted ${itemsToDelete.size} items"))
        }
    }
}