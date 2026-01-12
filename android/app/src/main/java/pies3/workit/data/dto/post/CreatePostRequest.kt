package pies3.workit.data.dto.post

data class CreatePostRequest(
    val title: String,
    val activityType: String,
    val body: String?,
    val imageUrl: String?,
    val location: String?,
    val groupId: String,
    val userId: String
)
