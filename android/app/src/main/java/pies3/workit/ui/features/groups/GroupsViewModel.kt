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
import pies3.workit.data.dto.group.GroupListResponse
import pies3.workit.data.dto.group.JoinGroupResponse
import pies3.workit.data.local.TokenManager
import pies3.workit.data.repository.GroupsRepository
import pies3.workit.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupsRepository: GroupsRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _createGroupState = MutableStateFlow<CreateGroupUiState>(CreateGroupUiState.Idle)
    val createGroupState: StateFlow<CreateGroupUiState> = _createGroupState.asStateFlow()

    private val _myGroupsState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val myGroupsState: StateFlow<GroupsUiState> = _myGroupsState.asStateFlow()

    private val _allGroupsState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val allGroupsState: StateFlow<GroupsUiState> = _allGroupsState.asStateFlow()

    private val _joinGroupState = MutableStateFlow<JoinGroupUiState>(JoinGroupUiState.Idle)
    val joinGroupState: StateFlow<JoinGroupUiState> = _joinGroupState.asStateFlow()

    init {
        loadMyGroups()
        loadAllGroups()
    }

    fun loadMyGroups() {
        viewModelScope.launch {
            try {
                _myGroupsState.value = GroupsUiState.Loading
                val userId = tokenManager.getUserId()

                if (userId == null) {
                    _myGroupsState.value = GroupsUiState.Error("Usuário não autenticado")
                    return@launch
                }

                val userResult = userRepository.getUserById(userId)

                if (userResult.isFailure) {
                    Log.e("GroupsViewModel", "Erro ao buscar usuário: ${userResult.exceptionOrNull()?.message}")
                    _myGroupsState.value = GroupsUiState.Error(
                        userResult.exceptionOrNull()?.message ?: "Erro ao carregar meus grupos"
                    )
                    return@launch
                }

                val user = userResult.getOrThrow()

                val groupsDetails = mutableListOf<GroupListResponse>()

                for (userGroup in user.groups) {
                    val groupResult = groupsRepository.getGroupById(userGroup.id)
                    if (groupResult.isSuccess) {
                        groupsDetails.add(groupResult.getOrThrow())
                    }
                }

                _myGroupsState.value = GroupsUiState.Success(groupsDetails)

            } catch (e: Exception) {
                Log.e("GroupsViewModel", "Exception ao carregar meus grupos", e)
                _myGroupsState.value = GroupsUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun loadAllGroups() {
        viewModelScope.launch {
            try {
                _allGroupsState.value = GroupsUiState.Loading

                val result = groupsRepository.getGroups()

                _allGroupsState.value = when {
                    result.isSuccess -> GroupsUiState.Success(result.getOrThrow())
                    else -> {
                        Log.e("GroupsViewModel", "Erro ao carregar grupos: ${result.exceptionOrNull()?.message}")
                        GroupsUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao carregar grupos")
                    }
                }
            } catch (e: Exception) {
                Log.e("GroupsViewModel", "Exception ao carregar grupos", e)
                _allGroupsState.value = GroupsUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

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
                    result.isSuccess -> {
                        loadMyGroups()
                        loadAllGroups()
                        CreateGroupUiState.Success(result.getOrThrow())
                    }
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

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            try {
                _joinGroupState.value = JoinGroupUiState.Loading

                val userId = tokenManager.getUserId()

                if (userId == null) {
                    Log.e("GroupsViewModel", "UserId é null - usuário não autenticado")
                    _joinGroupState.value = JoinGroupUiState.Error("Usuário não autenticado")
                    return@launch
                }

                val result = groupsRepository.joinGroup(groupId, userId)

                _joinGroupState.value = when {
                    result.isSuccess -> {
                       loadMyGroups()
                        loadAllGroups()
                        JoinGroupUiState.Success(result.getOrThrow())
                    }
                    else -> {
                        Log.e("GroupsViewModel", "Erro ao participar do grupo: ${result.exceptionOrNull()?.message}")
                        JoinGroupUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao participar do grupo")
                    }
                }
            } catch (e: Exception) {
                Log.e("GroupsViewModel", "Exception ao participar do grupo", e)
                _joinGroupState.value = JoinGroupUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun resetCreateGroupState() {
        _createGroupState.value = CreateGroupUiState.Idle
    }

    fun resetJoinGroupState() {
        _joinGroupState.value = JoinGroupUiState.Idle
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
    data class Success(val groups: List<GroupListResponse>) : GroupsUiState()
    data class Error(val message: String) : GroupsUiState()
}

sealed class JoinGroupUiState {
    object Idle : JoinGroupUiState()
    object Loading : JoinGroupUiState()
    data class Success(val result: JoinGroupResponse) : JoinGroupUiState()
    data class Error(val message: String) : JoinGroupUiState()
}