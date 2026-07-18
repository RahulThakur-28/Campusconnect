package com.rahul.campusconnect.presentation.lostfound.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.model.LostFoundItem
import com.rahul.campusconnect.presentation.lostfound.state.LostFoundTab
import com.rahul.campusconnect.presentation.lostfound.state.LostFoundUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LostFoundViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LostFoundUiState())
    val uiState: StateFlow<LostFoundUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network delay

            val dummyItems = listOf(
                LostFoundItem(
                    id = "1",
                    title = "Blue Water Bottle",
                    description = "Milton steel bottle with a blue cap. Left it in the library.",
                    category = "Personal",
                    location = "Main Library, 2nd Floor",
                    reportedBy = "Rahul Thakur",
                    reportedDate = "28 Jun",
                    status = "Lost",
                    contact = "9873327335"
                ),
                LostFoundItem(
                    id = "2",
                    title = "Scientific Calculator",
                    description = "Casio fx-991EX found near the cafeteria.",
                    category = "Electronics",
                    location = "Central Cafeteria",
                    reportedBy = "Anjali Sharma",
                    reportedDate = "29 Jun",
                    status = "Found",
                    contact = "9123456789"
                ),
                LostFoundItem(
                    id = "3",
                    title = "ID Card",
                    description = "Found near the entrance of Block B.",
                    category = "Document",
                    location = "Block B Entrance",
                    reportedBy = "Suresh Raina",
                    reportedDate = "30 Jun",
                    status = "Found",
                    contact = "9988776655"
                ),
                LostFoundItem(
                    id = "4",
                    title = "Black Wallet",
                    description = "Leather wallet containing some cash and a metro card.",
                    category = "Personal",
                    location = "Auditorium A",
                    reportedBy = "Priya Singh",
                    reportedDate = "01 Jul",
                    status = "Lost",
                    contact = "8877665544"
                )
            )

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    items = dummyItems
                )
            }
            applyFilters()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
        applyFilters()
    }

    fun onTabSelected(tab: LostFoundTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        applyFilters()
    }

    private fun applyFilters() {

        val state = _uiState.value

        val filtered = state.items.filter { item ->

            val matchesSearch =
                state.searchQuery.isBlank() ||
                        item.title.contains(state.searchQuery, ignoreCase = true) ||
                        item.description.contains(state.searchQuery, ignoreCase = true) ||
                        item.location.contains(state.searchQuery, ignoreCase = true)

            val matchesTab =
                item.status.equals(state.selectedTab.name, ignoreCase = true)

            matchesSearch && matchesTab
        }

        _uiState.update {
            it.copy(filteredItems = filtered)
        }
    }
}
