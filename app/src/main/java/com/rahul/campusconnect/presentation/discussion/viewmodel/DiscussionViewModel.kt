package com.rahul.campusconnect.presentation.discussion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.domain.model.*
import com.rahul.campusconnect.domain.repository.DiscussionRepository
import com.rahul.campusconnect.presentation.discussion.state.DiscussionSort
import com.rahul.campusconnect.presentation.discussion.state.DiscussionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscussionViewModel @Inject constructor(
    private val repository: DiscussionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussionUiState())
    val uiState = _uiState.asStateFlow()

    private val _moduleInfo = MutableStateFlow<Pair<DiscussionParentType, String>?>(null)

    init {
        // Observe User
        sessionManager.currentUser
            .onEach { user ->
                user?.let {
                    _uiState.update { state ->
                        state.copy(
                            currentUserId = it.uid,
                            currentUserRole = it.role.name
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        // Observe Discussions Reactively
        _moduleInfo
            .filterNotNull()
            .flatMapLatest { (type, id) ->
                repository.getDiscussions(type, id)
                    .onStart { _uiState.update { it.copy(isLoading = true) } }
                    .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            }
            .onEach { list ->
                _uiState.update { it.copy(discussions = list, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun setModule(moduleType: DiscussionParentType, moduleId: String) {
        if (_moduleInfo.value?.first == moduleType && _moduleInfo.value?.second == moduleId) return
        _moduleInfo.value = moduleType to moduleId
    }

    fun askQuestion(title: String, question: String) {
        val user = sessionManager.getCurrentUser() ?: run {
            _uiState.update { it.copy(error = "User session not found. Please log in again.") }
            return
        }
        
        val moduleInfo = _moduleInfo.value ?: run {
            _uiState.update { it.copy(error = "Discussion context not initialized.") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true) }
            val discussion = Discussion(
                collegeId = user.collegeId,
                moduleType = moduleInfo.first,
                moduleId = moduleInfo.second,
                title = title,
                question = question,
                createdBy = user.uid,
                createdByName = user.fullName,
                createdByPhoto = user.profileImage,
                createdByRole = user.role,
                isVerified = user.isVerified
            )
            repository.askQuestion(discussion)
                .onSuccess { 
                    _uiState.update { it.copy(isActionLoading = false, successMessage = "Question posted successfully") }
                }
                .onFailure { e -> 
                    _uiState.update { it.copy(isActionLoading = false, error = e.message) }
                }
        }
    }

    fun answerQuestion(discussionId: String, message: String) {
        val user = sessionManager.getCurrentUser() ?: return
        
        if (user.role == UserRole.STUDENT) {
            _uiState.update { it.copy(error = "Students cannot answer questions. Get verified to contribute.") }
            return
        }

        viewModelScope.launch {
            val isOfficial = user.role != UserRole.VERIFIED_STUDENT
            val reply = Reply(
                discussionId = discussionId,
                message = message,
                createdBy = user.uid,
                createdByName = user.fullName,
                createdByPhoto = user.profileImage,
                createdByRole = user.role,
                isVerified = user.isVerified,
                isOfficial = isOfficial
            )
            repository.answerQuestion(reply)
        }
    }

    fun loadReplies(discussionId: String) {
        repository.getReplies(discussionId)
            .onEach { list ->
                _uiState.update { state ->
                    val newMap = state.repliesMap.toMutableMap()
                    newMap[discussionId] = list
                    state.copy(repliesMap = newMap)
                }
            }
            .launchIn(viewModelScope)
    }

    fun likeQuestion(discussionId: String) {
        val userId = sessionManager.getUid() ?: return
        viewModelScope.launch {
            repository.likeQuestion(discussionId, userId)
        }
    }

    fun likeReply(discussionId: String, replyId: String) {
        val userId = sessionManager.getUid() ?: return
        viewModelScope.launch {
            repository.likeReply(discussionId, replyId, userId)
        }
    }

    fun deleteQuestion(discussionId: String) {
        viewModelScope.launch { 
            _uiState.update { it.copy(isActionLoading = true) }
            repository.deleteQuestion(discussionId)
                .onSuccess { 
                    _uiState.update { it.copy(isActionLoading = false, successMessage = "Question deleted") }
                }
                .onFailure { e -> 
                    _uiState.update { it.copy(isActionLoading = false, error = e.message) }
                }
        }
    }

    fun editQuestion(discussionId: String, title: String, question: String) {
        viewModelScope.launch { repository.editQuestion(discussionId, title, question) }
    }

    fun deleteReply(discussionId: String, replyId: String) {
        viewModelScope.launch { 
            _uiState.update { it.copy(isActionLoading = true) }
            repository.deleteReply(discussionId, replyId)
                .onSuccess { 
                    _uiState.update { it.copy(isActionLoading = false, successMessage = "Reply deleted") }
                }
                .onFailure { e -> 
                    _uiState.update { it.copy(isActionLoading = false, error = e.message) }
                }
        }
    }

    fun editReply(discussionId: String, replyId: String, message: String) {
        viewModelScope.launch { repository.editReply(discussionId, replyId, message) }
    }

    fun report(id: String, type: String, reason: String) {
        viewModelScope.launch { repository.report(id, type, reason) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSortChanged(sort: DiscussionSort) {
        _uiState.update { it.copy(sortBy = sort) }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }
}
