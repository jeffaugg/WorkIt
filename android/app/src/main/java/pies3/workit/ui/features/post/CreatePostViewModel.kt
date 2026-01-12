package pies3.workit.ui.features.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pies3.workit.data.dto.post.PostResponse
import pies3.workit.data.local.TokenManager
import pies3.workit.data.repository.PostsRepository
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _createPostState = MutableStateFlow<CreatePostUiState>(CreatePostUiState.Idle)
    val createPostState: StateFlow<CreatePostUiState> = _createPostState.asStateFlow()

    fun createPost(
        title: String,
        activityType: String,
        body: String?,
        imageUrl: String?,
        location: String?,
        groupId: String
    ) {
        viewModelScope.launch {
            try {
                _createPostState.value = CreatePostUiState.Loading

                val userId = tokenManager.getUserId()

                if (userId == null) {
                    Log.e("CreatePostViewModel", "UserId é null - usuário não autenticado")
                    _createPostState.value = CreatePostUiState.Error("Usuário não autenticado")
                    return@launch
                }

                val result = postsRepository.createPost(
                    title = title,
                    activityType = activityType,
                    body = body,
                    imageUrl = imageUrl,
                    location = location,
                    groupId = groupId,
                    userId = userId
                )

                _createPostState.value = when {
                    result.isSuccess -> CreatePostUiState.Success(result.getOrThrow())
                    else -> {
                        Log.e("CreatePostViewModel", "Erro ao criar publicação: ${result.exceptionOrNull()?.message}")
                        CreatePostUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao criar publicação")
                    }
                }
            } catch (e: Exception) {
                Log.e("CreatePostViewModel", "Exception ao criar publicação", e)
                _createPostState.value = CreatePostUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun resetState() {
        _createPostState.value = CreatePostUiState.Idle
    }
}

sealed interface CreatePostUiState {
    data object Idle : CreatePostUiState
    data object Loading : CreatePostUiState
    data class Success(val post: PostResponse) : CreatePostUiState
    data class Error(val message: String) : CreatePostUiState
}
