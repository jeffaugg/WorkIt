package pies3.workit.data.dto.post

data class UpdatePostRequest(
    val title: String,
    val activityType: String,
    val body: String?,
    val imageUrl: String?,
    val location: String?,
    val groupId: String
)
