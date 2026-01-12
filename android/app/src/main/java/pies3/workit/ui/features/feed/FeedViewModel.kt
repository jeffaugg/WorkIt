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
class FeedViewModel @Inject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _feedState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val feedState: StateFlow<FeedUiState> = _feedState.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _feedState.value = FeedUiState.Loading

                val result = postsRepository.getPosts()

                _feedState.value = when {
                    result.isSuccess -> {
                        val posts = result.getOrThrow()
                        if (posts.isEmpty()) {
                            FeedUiState.Empty
                        } else {
                            FeedUiState.Success(posts)
                        }
                    }
                    else -> {
                        Log.e("FeedViewModel", "Erro ao carregar posts: ${result.exceptionOrNull()?.message}")
                        FeedUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar posts")
                    }
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Exception ao carregar posts", e)
                _feedState.value = FeedUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun refresh() {
        loadPosts()
    }
}

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data object Empty : FeedUiState
    data class Success(val posts: List<PostResponse>) : FeedUiState
    data class Error(val message: String) : FeedUiState
}
