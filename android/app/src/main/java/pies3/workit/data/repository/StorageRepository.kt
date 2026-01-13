package pies3.workit.data.repository

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import pies3.workit.data.api.StorageApi
import pies3.workit.data.utils.uriToMultipartFile
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storageApi: StorageApi,
    @ApplicationContext private val context: Context
) {
    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val part = uriToMultipartFile(context, uri)
            val response = storageApi.upload(part)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.url)
            } else {
                Result.failure(Exception("Erro ao enviar imagem"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
