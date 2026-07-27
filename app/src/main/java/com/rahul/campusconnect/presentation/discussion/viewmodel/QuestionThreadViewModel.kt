package com.rahul.campusconnect.presentation.discussion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.domain.model.Answer
import com.rahul.campusconnect.domain.model.Question
import com.rahul.campusconnect.domain.repository.EventQARepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionThreadViewModel @Inject constructor(
    private val repository: EventQARepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _question = MutableStateFlow<Question?>(null)
    val question = _question.asStateFlow()

    private val _answers = MutableStateFlow<List<Answer>>(emptyList())
    val answers = _answers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadThread(questionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Load Question Details
            repository.getQuestionById(questionId)
                .onSuccess { _question.value = it }
                .onFailure { _error.value = it.message }

            // Observe Answers Realtime
            repository.getAnswers(questionId)
                .onEach { 
                    _answers.value = it 
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
            val answer = Answer(
                questionId = currentQuestion.id,
                parentId = currentQuestion.parentId,
                parentType = currentQuestion.parentType,
                userId = user.uid,
                userName = user.fullName,
                userRole = user.role,
                profileImage = user.profileImage,
                content = content,
                collegeId = user.collegeId
            )
            repository.answerQuestion(answer)
        }
    }

    fun likeQuestion(id: String) {
        viewModelScope.launch { repository.likeQuestion(id) }
    }

    fun likeAnswer(id: String) {
        viewModelScope.launch { repository.likeAnswer(_question.value?.id ?: "", id) }
    }
}
