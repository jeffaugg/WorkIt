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

    private val _leaveGroupState = MutableStateFlow<LeaveGroupUiState>(LeaveGroupUiState.Idle)
    val leaveGroupState: StateFlow<LeaveGroupUiState> = _leaveGroupState.asStateFlow()

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

                val userId = tokenManager.getUserId()
                val token = tokenManager.getToken()

                if (token == null || userId == null) {
                    Log.e("GroupsViewModel", "Token ou UserId é null - usuário não autenticado")
                    _createGroupState.value = CreateGroupUiState.Error("Usuário não autenticado")
                    return@launch
                }

                val createResult = groupsRepository.createGroup(name, description, imageUrl)

                if (createResult.isFailure) {
                    Log.e("GroupsViewModel", "Erro ao criar grupo: ${createResult.exceptionOrNull()?.message}")
                    _createGroupState.value = CreateGroupUiState.Error(
                        createResult.exceptionOrNull()?.message ?: "Erro ao criar grupo"
                    )
                    return@launch
                }

                val createdGroup = createResult.getOrThrow()

                val joinResult = groupsRepository.joinGroup(createdGroup.id, userId)

                if (joinResult.isFailure) {
                    Log.e("GroupsViewModel", "Grupo criado mas erro ao adicionar criador: ${joinResult.exceptionOrNull()?.message}")
                }

                loadMyGroups()
                loadAllGroups()

                _createGroupState.value = CreateGroupUiState.Success(createdGroup)

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

    fun leaveGroup(groupId: String) {
        viewModelScope.launch {
            try {
                _leaveGroupState.value = LeaveGroupUiState.Loading

                val userId = tokenManager.getUserId()

                if (userId == null) {
                    Log.e("GroupsViewModel", "UserId é null - usuário não autenticado")
                    _leaveGroupState.value = LeaveGroupUiState.Error("Usuário não autenticado")
                    return@launch
                }

                val result = groupsRepository.leaveGroup(groupId, userId)

                _leaveGroupState.value = when {
                    result.isSuccess -> {
                        loadMyGroups()
                        loadAllGroups()
                        LeaveGroupUiState.Success
                    }
                    else -> {
                        Log.e("GroupsViewModel", "Erro ao sair do grupo: ${result.exceptionOrNull()?.message}")
                        LeaveGroupUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao sair do grupo")
                    }
                }
            } catch (e: Exception) {
                Log.e("GroupsViewModel", "Exception ao sair do grupo", e)
                _leaveGroupState.value = LeaveGroupUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun resetCreateGroupState() {
        _createGroupState.value = CreateGroupUiState.Idle
    }

    fun resetJoinGroupState() {
        _joinGroupState.value = JoinGroupUiState.Idle
    }

    fun resetLeaveGroupState() {
        _leaveGroupState.value = LeaveGroupUiState.Idle
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

sealed class LeaveGroupUiState {
    object Idle : LeaveGroupUiState()
    object Loading : LeaveGroupUiState()
    object Success : LeaveGroupUiState()
    data class Error(val message: String) : LeaveGroupUiState()
}