package pies3.workit.data.dto.auth

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)
