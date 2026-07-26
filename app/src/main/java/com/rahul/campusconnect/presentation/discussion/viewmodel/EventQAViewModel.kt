package com.rahul.campusconnect.presentation.discussion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Question
import com.rahul.campusconnect.domain.repository.EventQARepository
import com.rahul.campusconnect.presentation.discussion.state.EventQAUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventQAViewModel @Inject constructor(
    private val repository: EventQARepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventQAUiState())
    val uiState: StateFlow<EventQAUiState> = _uiState.asStateFlow()

    fun loadQuestions(parentId: String, parentType: DiscussionParentType) {
        repository.getQuestions(parentId, parentType)
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .onEach { questions ->
                _uiState.update { it.copy(questions = questions, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onLikeQuestion(questionId: String) {
        viewModelScope.launch {
            repository.likeQuestion(questionId)
        }
    }

    fun askQuestion(content: String, parentId: String, parentType: DiscussionParentType, userId: String, userName: String) {
        val question = Question(
            parentId = parentId,
            parentType = parentType,
            userId = userId,
            userName = userName,
            content = content
        )
        viewModelScope.launch {
            repository.askQuestion(question)
        }
    }
}
