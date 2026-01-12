package pies3.workit.ui.features.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pies3.workit.data.dto.user.UserGroup
import pies3.workit.data.local.TokenManager
import pies3.workit.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _groupsState = MutableStateFlow<GroupsUiState>(GroupsUiState.Idle)
    val groupsState: StateFlow<GroupsUiState> = _groupsState.asStateFlow()

    fun loadMyGroups() {
        viewModelScope.launch {
            _groupsState.value = GroupsUiState.Loading

            val userId = tokenManager.getUserId()
            if (userId.isNullOrBlank()) {
                _groupsState.value = GroupsUiState.Error("Usuário não identificado. Faça login novamente.")
                return@launch
            }

            userRepository.getUserById(userId)
                .onSuccess { user ->
                    val groups = user.groups ?: emptyList() // se groups não for nullable, simplifica
                    _groupsState.value = GroupsUiState.Success(groups)
                }
                .onFailure { error ->
                    _groupsState.value = GroupsUiState.Error(
                        error.message ?: "Erro ao carregar seus grupos"
                    )
                }
        }
    }
}

sealed class GroupsUiState {
    data object Idle : GroupsUiState()
    data object Loading : GroupsUiState()
    data class Success(val groups: List<UserGroup>) : GroupsUiState()
    data class Error(val message: String) : GroupsUiState()
}
