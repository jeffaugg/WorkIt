package pies3.workit.data.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

private fun getFileName(context: Context, uri: Uri): String {
    val resolver = context.contentResolver
    val cursor = resolver.query(uri, null, null, null, null)
        ?: return "upload_${System.currentTimeMillis()}"

    cursor.use {
        val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx >= 0 && it.moveToFirst()) return it.getString(idx)
    }

    return "upload_${System.currentTimeMillis()}"
}

fun uriToMultipartFile(context: Context, uri: Uri): MultipartBody.Part {
    val resolver = context.contentResolver
    val mimeType = resolver.getType(uri) ?: "application/octet-stream"
    val fileName = getFileName(context, uri)

    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
        ?: throw IllegalStateException("Não foi possível ler o arquivo: $uri")

    val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("file", fileName, body)
}
