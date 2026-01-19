package pies3.workit.data.dto.user

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null
)