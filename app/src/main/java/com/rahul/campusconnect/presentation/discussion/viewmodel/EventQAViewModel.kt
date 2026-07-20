package com.rahul.campusconnect.presentation.discussion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.data.repository.FakeEventQARepository
import com.rahul.campusconnect.presentation.discussion.state.EventQAUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventQAViewModel @Inject constructor(
    private val repository: FakeEventQARepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventQAUiState())
    val uiState: StateFlow<EventQAUiState> = _uiState.asStateFlow()

    fun loadQuestions(parentId: String, parentType: DiscussionParentType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getQuestions(parentId, parentType).collect { questions ->
                _uiState.update { it.copy(questions = questions, isLoading = false) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onLikeQuestion(questionId: String) {
        viewModelScope.launch {
            repository.likeQuestion(questionId)
            // In real app, we would refresh or update local state
        }
    }
}