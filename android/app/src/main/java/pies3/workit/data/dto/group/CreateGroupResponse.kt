package pies3.workit.data.dto.group

data class CreateGroupResponse (
    val id: String,
    val name: String,
    val imgUrl: String?,
    val description: String?,
    val createdAt: String,
    val updatedAt: String
)