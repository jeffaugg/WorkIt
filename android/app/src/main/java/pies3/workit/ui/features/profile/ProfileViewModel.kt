package pies3.workit.ui.features.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pies3.workit.data.dto.user.UserResponse
import pies3.workit.data.local.TokenManager
import pies3.workit.data.repository.StorageRepository
import pies3.workit.data.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateState: StateFlow<UpdateProfileState> = _updateState.asStateFlow()

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

    fun updateProfile(
        name: String,
        email: String,
        description: String,
        birthDate: String,
        photoUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                _updateState.value = UpdateProfileState.Loading

                val userId = tokenManager.getUserId()
                if (userId == null) {
                    _updateState.value = UpdateProfileState.Error("Usuário não autenticado")
                    return@launch
                }

                // Atualiza nome e email via API
                val result = userRepository.updateUser(userId, name, email)

                if (result.isSuccess) {
                    // Recarrega o perfil com os dados atualizados
                    loadProfile()
                    _updateState.value = UpdateProfileState.Success

                    // TODO: Implementar atualização de description, birthDate e photoUri
                    // quando a API tiver suporte para esses campos
                    Log.d("ProfileViewModel", "Description: $description, BirthDate: $birthDate, PhotoUri: $photoUri")
                } else {
                    _updateState.value = UpdateProfileState.Error(
                        result.exceptionOrNull()?.message ?: "Erro ao atualizar perfil"
                    )
                }

            } catch (e: Exception) {
                _updateState.value = UpdateProfileState.Error(
                    e.message ?: "Erro ao atualizar perfil"
                )
            }
        }
    }

    fun resetLogoutState() {
        _logoutState.value = LogoutState.Idle
    }

    fun resetUpdateState() {
        _updateState.value = UpdateProfileState.Idle
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
            "Membro desde ${createdAt.substring(0, 10)}"
        }
    }

    fun updateProfilePhotoWithUpload(photoUri: Uri) {
        viewModelScope.launch {
            try {
                _updateState.value = UpdateProfileState.Loading

                val userId = tokenManager.getUserId()
                if (userId == null) {
                    _updateState.value = UpdateProfileState.Error("Usuário não autenticado")
                    return@launch
                }

                val uploadResult = storageRepository.uploadImage(photoUri)
                if (uploadResult.isFailure) {
                    _updateState.value = UpdateProfileState.Error(
                        uploadResult.exceptionOrNull()?.message ?: "Erro ao enviar imagem"
                    )
                    return@launch
                }

                val imageUrl = uploadResult.getOrNull()
                if (imageUrl.isNullOrBlank()) {
                    _updateState.value = UpdateProfileState.Error("URL retornou vazia")
                    return@launch
                }

                val result = userRepository.updateUser(userId = userId, avatarUrl = imageUrl)

                if (result.isSuccess) {
                    loadProfile()
                    _updateState.value = UpdateProfileState.Success
                } else {
                    _updateState.value = UpdateProfileState.Error(
                        result.exceptionOrNull()?.message ?: "Erro ao atualizar foto do perfil"
                    )
                }
            } catch (e: Exception) {
                _updateState.value = UpdateProfileState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

}



sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: UserResponse) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class LogoutState {
    object Idle : LogoutState()
    object Loading : LogoutState()
    object Success : LogoutState()
    data class Error(val message: String) : LogoutState()
}

sealed class UpdateProfileState {
    object Idle : UpdateProfileState()
    object Loading : UpdateProfileState()
    object Success : UpdateProfileState()
    data class Error(val message: String) : UpdateProfileState()
}
