package pies3.workit.ui.features.feed

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
class GroupFeedViewModel @Inject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _feedState = MutableStateFlow<GroupFeedUiState>(GroupFeedUiState.Loading)
    val feedState: StateFlow<GroupFeedUiState> = _feedState.asStateFlow()

    fun loadGroupPosts(groupId: String) {
        viewModelScope.launch {
            try {
                _feedState.value = GroupFeedUiState.Loading

                val result = postsRepository.getPostsByGroup(groupId)

                _feedState.value = when {
                    result.isSuccess -> {
                        val posts = result.getOrThrow()
                        if (posts.isEmpty()) {
                            GroupFeedUiState.Empty
                        } else {
                            GroupFeedUiState.Success(posts)
                        }
                    }
                    else -> {
                        Log.e("GroupFeedViewModel", "Erro ao carregar posts: ${result.exceptionOrNull()?.message}")
                        GroupFeedUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar posts")
                    }
                }
            } catch (e: Exception) {
                Log.e("GroupFeedViewModel", "Exception ao carregar posts", e)
                _feedState.value = GroupFeedUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun refresh(groupId: String) {
        loadGroupPosts(groupId)
    }
}

sealed interface GroupFeedUiState {
    data object Loading : GroupFeedUiState
    data object Empty : GroupFeedUiState
    data class Success(val posts: List<PostResponse>) : GroupFeedUiState
    data class Error(val message: String) : GroupFeedUiState
}
