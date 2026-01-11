package pies3.workit.data.dto.group

data class CreateGroupRequest(
    val name: String,
    val description: String?,
    val imageUrl: String?
)
