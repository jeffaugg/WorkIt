package pies3.workit.data.dto.user

data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val createdAt: String,
    val updatedAt: String,
    val groups: List<UserGroup>
)

data class UserGroup(
    val id: String,
    val name: String
)