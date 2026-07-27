package com.rahul.campusconnect.presentation.discussion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.domain.model.*
import com.rahul.campusconnect.domain.repository.DiscussionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionThreadViewModel @Inject constructor(
    private val repository: DiscussionRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _question = MutableStateFlow<Discussion?>(null)
    val question = _question.asStateFlow()

    private val _answers = MutableStateFlow<List<Reply>>(emptyList())
    val answers = _answers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _currentUserId = MutableStateFlow("")
    val currentUserId = _currentUserId.asStateFlow()

    private val _currentUserRole = MutableStateFlow("")
    val currentUserRole = _currentUserRole.asStateFlow()

    init {
        sessionManager.getCurrentUser()?.let {
            _currentUserId.value = it.uid
            _currentUserRole.value = it.role.name
        }
    }

    fun loadThread(questionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.getDiscussionById(questionId)
                .onSuccess { _question.value = it }
                .onFailure { _error.value = it.message }

            repository.getReplies(questionId)
                .onEach { 
                    _answers.value = it.filter { !it.isDeleted }
                    _isLoading.value = false
                }
                .catch { _error.value = it.message }
                .launchIn(viewModelScope)
        }
    }

    fun submitAnswer(content: String) {
        val user = sessionManager.getCurrentUser() ?: return
        val currentQuestion = _question.value ?: return
        
        viewModelScope.launch {
            val isOfficial = user.role == UserRole.VERIFIED_TEACHER || 
                           user.role == UserRole.PLACEMENT_CELL || 
                           user.role == UserRole.ADMIN || 
                           user.role == UserRole.SUPER_ADMIN

            val reply = Reply(
                discussionId = currentQuestion.discussionId,
                message = content,
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

    fun likeQuestion(id: String) {
        val userId = sessionManager.getUid() ?: return
        viewModelScope.launch { repository.likeQuestion(id, userId) }
    }

    fun likeAnswer(replyId: String) {
        val userId = sessionManager.getUid() ?: return
        val discussionId = _question.value?.discussionId ?: return
        viewModelScope.launch { repository.likeReply(discussionId, replyId, userId) }
    }

    fun deleteQuestion() {
        val id = _question.value?.discussionId ?: return
        viewModelScope.launch { repository.deleteQuestion(id) }
    }

    fun deleteReply(replyId: String) {
        val discussionId = _question.value?.discussionId ?: return
        viewModelScope.launch { repository.deleteReply(discussionId, replyId) }
    }

    fun editQuestion(title: String, question: String) {
        val id = _question.value?.discussionId ?: return
        viewModelScope.launch { repository.editQuestion(id, title, question) }
    }

    fun editReply(replyId: String, message: String) {
        val discussionId = _question.value?.discussionId ?: return
        viewModelScope.launch { repository.editReply(discussionId, replyId, message) }
    }

    fun report(id: String, type: String, reason: String) {
        viewModelScope.launch { repository.report(id, type, reason) }
    }
}
