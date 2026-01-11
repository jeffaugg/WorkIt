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
import pies3.workit.data.repository.PostsRepository
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _postState = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val postState: StateFlow<PostDetailState> = _postState.asStateFlow()

    private val _deleteState = MutableStateFlow<PostDeleteState>(PostDeleteState.Idle)
    val deleteState: StateFlow<PostDeleteState> = _deleteState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            try {
                _postState.value = PostDetailState.Loading

                val result = postsRepository.getPostById(postId)

                _postState.value = when {
                    result.isSuccess -> PostDetailState.Success(result.getOrThrow())
                    else -> {
                        Log.e("PostDetailViewModel", "Erro ao carregar post: ${result.exceptionOrNull()?.message}")
                        PostDetailState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar publicação")
                    }
                }
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Exception ao carregar post", e)
                _postState.value = PostDetailState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                _deleteState.value = PostDeleteState.Loading

                val result = postsRepository.deletePost(postId)

                _deleteState.value = when {
                    result.isSuccess -> PostDeleteState.Success
                    else -> {
                        Log.e("PostDetailViewModel", "Erro ao excluir post: ${result.exceptionOrNull()?.message}")
                        PostDeleteState.Error(result.exceptionOrNull()?.message ?: "Erro ao excluir publicação")
                    }
                }
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Exception ao excluir post", e)
                _deleteState.value = PostDeleteState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}

sealed interface PostDetailState {
    data object Loading : PostDetailState
    data class Success(val post: PostResponse) : PostDetailState
    data class Error(val message: String) : PostDetailState
}

sealed interface PostDeleteState {
    data object Idle : PostDeleteState
    data object Loading : PostDeleteState
    data object Success : PostDeleteState
    data class Error(val message: String) : PostDeleteState
}
