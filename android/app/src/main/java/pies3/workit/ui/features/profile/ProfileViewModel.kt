package pies3.workit.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pies3.workit.data.dto.user.UserResponse
import pies3.workit.data.local.TokenManager
import pies3.workit.data.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                _profileState.value = ProfileState.Loading

                val userId = tokenManager.getUserId()

                if (userId == null) {
                    _profileState.value = ProfileState.Error("Usuário não autenticado")
                    return@launch
                }

                val result = userRepository.getCurrentUser(userId)

                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    _profileState.value = ProfileState.Success(user)
                } else {
                    _profileState.value = ProfileState.Error(
                        result.exceptionOrNull()?.message ?: "Erro ao carregar perfil"
                    )
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(
                    e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading

            try {
                tokenManager.clearToken()
                _logoutState.value = LogoutState.Success
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error(
                    e.message ?: "Erro ao fazer logout"
                )
            }
        }
    }

    fun resetLogoutState() {
        _logoutState.value = LogoutState.Idle
    }

    fun getInitials(name: String): String {
        return name.split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
    }

    fun formatMemberSince(createdAt: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(createdAt)

            val outputFormat = SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR"))
            "Membro desde ${outputFormat.format(date!!)}"
        } catch (e: Exception) {
            "Membro desde 2024"
        }
    }
}

sealed class LogoutState {
    object Idle : LogoutState()
    object Loading : LogoutState()
    object Success : LogoutState()
    data class Error(val message: String) : LogoutState()
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: UserResponse) : ProfileState()
    data class Error(val message: String) : ProfileState()
}
