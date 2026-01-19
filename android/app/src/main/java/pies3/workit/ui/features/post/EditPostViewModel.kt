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
class EditPostViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _postState = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val postState: StateFlow<PostDetailState> = _postState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdatePostUiState>(UpdatePostUiState.Idle)
    val updateState: StateFlow<UpdatePostUiState> = _updateState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            try {
                _postState.value = PostDetailState.Loading

                val currentUserId = tokenManager.getUserId()
                val result = postsRepository.getPostById(postId)

                _postState.value = when {
                    result.isSuccess -> {
                        val post = result.getOrThrow()
                        val isOwner = post.user.id == currentUserId

                        PostDetailState.Success(post, isOwner)
                    }
                    else -> {
                        Log.e("EditPostViewModel", "Erro ao carregar post: ${result.exceptionOrNull()?.message}")
                        PostDetailState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar publicação")
                    }
                }
            } catch (e: Exception) {
                Log.e("EditPostViewModel", "Exception ao carregar post", e)
                _postState.value = PostDetailState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun updatePost(
        postId: String,
        title: String,
        activityType: String,
        body: String?,
        imageUrl: String?,
        location: String?,
        groupId: String
    ) {
        viewModelScope.launch {
            try {
                _updateState.value = UpdatePostUiState.Loading

                val result = postsRepository.updatePost(
                    postId = postId,
                    title = title,
                    activityType = activityType,
                    body = body,
                    imageUrl = imageUrl,
                    location = location,
                    groupId = groupId
                )

                _updateState.value = when {
                    result.isSuccess -> UpdatePostUiState.Success(result.getOrThrow())
                    else -> {
                        Log.e("EditPostViewModel", "Erro ao atualizar post: ${result.exceptionOrNull()?.message}")
                        UpdatePostUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao atualizar publicação")
                    }
                }
            } catch (e: Exception) {
                Log.e("EditPostViewModel", "Exception ao atualizar post", e)
                _updateState.value = UpdatePostUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun resetState() {
        _updateState.value = UpdatePostUiState.Idle
    }
}

sealed interface UpdatePostUiState {
    data object Idle : UpdatePostUiState
    data object Loading : UpdatePostUiState
    data class Success(val post: PostResponse) : UpdatePostUiState
    data class Error(val message: String) : UpdatePostUiState
}
