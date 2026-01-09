package pies3.workit.ui.features.profile.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import pies3.workit.R
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, description: String, birthDate: String, photoUri: Uri?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var name by rememberSaveable { mutableStateOf("Alex Thompson") }
    var email by rememberSaveable { mutableStateOf("alex.thompson@email.com") }
    var description by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("01/01/1990") }
    var selectedPhotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var isDatePickerVisible by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedPhotoUri = uri }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Editar Perfil", style = MaterialTheme.typography.headlineSmall)

            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = selectedPhotoUri ?: R.drawable.ic_default_profile,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                IconButton(onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Change Profile Picture")
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Adicione uma descrição!")},
                singleLine = false
            )
            OutlinedTextField(
                value = birthDate,
                onValueChange = {},
                label = { Text("Data de Nascimento") },
                leadingIcon = { Icon(Icons.Default.DateRange, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { isDatePickerVisible = true },
                readOnly = true,
            )

            Button(
                onClick = {
                    onSave(name, email, description, birthDate, selectedPhotoUri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }

        if (isDatePickerVisible) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { isDatePickerVisible = false },
                confirmButton = {
                    TextButton(onClick = {
                        isDatePickerVisible = false
                        birthDate = datePickerState.selectedDateMillis?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                        } ?: ""
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isDatePickerVisible = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
