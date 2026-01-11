package pies3.workit.ui.features.groups

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pies3.workit.data.dto.group.CreateGroupResponse
import pies3.workit.data.local.TokenManager
import pies3.workit.data.repository.GroupsRepository
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupsRepository: GroupsRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _createGroupState = MutableStateFlow<CreateGroupUiState>(CreateGroupUiState.Idle)
    val createGroupState: StateFlow<CreateGroupUiState> = _createGroupState.asStateFlow()

    private val _groupsState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val groupsState: StateFlow<GroupsUiState> = _groupsState.asStateFlow()

    fun createGroup(name: String, description: String?, imageUrl: String?) {
        viewModelScope.launch {
            try {
                _createGroupState.value = CreateGroupUiState.Loading

                val token = tokenManager.getToken()

                if (token == null) {
                    Log.e("GroupsViewModel", "Token é null - usuário não autenticado")
                    _createGroupState.value = CreateGroupUiState.Error("Usuário não autenticado")
                    return@launch
                }

                val result = groupsRepository.createGroup(name, description, imageUrl)

                _createGroupState.value = when {
                    result.isSuccess -> CreateGroupUiState.Success(result.getOrThrow())
                    else -> {
                        Log.e("GroupsViewModel", "Erro ao criar grupo: ${result.exceptionOrNull()?.message}")
                        CreateGroupUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao criar grupo")
                    }
                }
            } catch (e: Exception) {
                Log.e("GroupsViewModel", "Exception ao criar grupo", e)
                _createGroupState.value = CreateGroupUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun resetCreateGroupState() {
        _createGroupState.value = CreateGroupUiState.Idle
    }

}

sealed class CreateGroupUiState {
    object Idle : CreateGroupUiState()
    object Loading : CreateGroupUiState()
    data class Success(val group: CreateGroupResponse) : CreateGroupUiState()
    data class Error(val message: String) : CreateGroupUiState()
}

sealed class GroupsUiState {
    object Loading : GroupsUiState()
    data class Success(val groups: List<CreateGroupResponse>) : GroupsUiState()
    data class Error(val message: String) : GroupsUiState()
}
