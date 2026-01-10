package pies3.workit.ui.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pies3.workit.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank()) {
            _uiState.value = RegisterUiState.Error("Nome não pode ser vazio")
            return
        }

        if (email.isBlank()) {
            _uiState.value = RegisterUiState.Error("E-mail não pode ser vazio")
            return
        }

        if (password.length < 8) {
            _uiState.value = RegisterUiState.Error("Senha deve ter pelo menos 8 caracteres")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = RegisterUiState.Error("As senhas não coincidem")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            authRepository.register(name, email, password)
                .onSuccess { response ->
                    _uiState.value = RegisterUiState.Success(response.token)
                }
                .onFailure { error ->
                    _uiState.value = RegisterUiState.Error(
                        error.message ?: "Erro desconhecido ao criar conta"
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val token: String) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
