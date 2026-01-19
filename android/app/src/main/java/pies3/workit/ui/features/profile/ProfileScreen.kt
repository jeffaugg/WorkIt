package pies3.workit.ui.features.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pies3.workit.ui.features.profile.components.*
import androidx.activity.result.PickVisualMediaRequest

@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    showEditModal: Boolean = false,
    onModalShown: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val logoutState by viewModel.logoutState.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var notifyNewPost by rememberSaveable { mutableStateOf(true) }
    var notifyGroupInvites by rememberSaveable { mutableStateOf(true) }
    var notifyWeeklyDigest by rememberSaveable { mutableStateOf(false) }
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }

    var selectedProfileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedProfileImageUri = uri
        if (uri != null) {
            viewModel.updateProfilePhotoWithUpload(uri)
        }
    }


    if (showEditModal) {
        LaunchedEffect(Unit) {
            isSheetOpen = true
            onModalShown()
        }
    }

    LaunchedEffect(logoutState) {
        when (val state = logoutState) {
            is LogoutState.Success -> {
                onLogout()
                snackbarHostState.showSnackbar("Logout realizado com sucesso!")
                viewModel.resetLogoutState()
            }
            is LogoutState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetLogoutState()
            }
            else -> {}
        }
    }

    LaunchedEffect(updateState) {
        when (val state = updateState) {
            is UpdateProfileState.Success -> {
                snackbarHostState.showSnackbar("Perfil atualizado com sucesso!")
                viewModel.resetUpdateState()
            }
            is UpdateProfileState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    if (isSheetOpen) {
        val user = (profileState as? ProfileState.Success)?.user

        EditProfileSheet(
            isNewUser = showEditModal,
            initialName = user?.name ?: "",
            initialEmail = user?.email ?: "",
            initialDescription = "",
            initialBirthDate = "18/12/2000",
            onDismiss = { isSheetOpen = false },
            onSave = { name, email, description, birthDate, photoUri ->
                Log.d("ProfileScreen", "Name: $name, Description: $description, Email: $email, Birth Date: $birthDate, Photo Uri: $photoUri")
                viewModel.updateProfile(name, email, description, birthDate, photoUri)
                isSheetOpen = false
            }
        )
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when (val state = profileState) {
            is ProfileState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProfileState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            is ProfileState.Success -> {
                val user = state.user
                println(user.avatarUrl)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProfileHeader(
                        name = user.name,
                        email = user.email,
                        description = "Corredor, amante dos esportes!",
                        memberSince = viewModel.formatMemberSince(user.createdAt),
                        initials = viewModel.getInitials(user.name),
                        photoUrl = user.avatarUrl,
                        onChangePhotoClick = {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onEditClick = { isSheetOpen = true }
                    )


                    ProfileSectionCard(title = "Aparência", icon = Icons.Outlined.Build) {
                        Text(
                            text = "Personalize a aparência do aplicativo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        NotificationRow(
                            title = "Modo Escuro",
                            subtitle = "Alternar entre temas claro e escuro",
                            checked = isDarkTheme,
                            onCheckedChange = { isChecked ->
                                onThemeChange(isChecked)
                            }
                        )
                    }

                    ProfileSectionCard(title = "Notificações", icon = Icons.Default.Notifications) {
                        Text(
                            text = "Gerencie suas preferências de notificação",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        NotificationRow(
                            title = "Novos Posts",
                            subtitle = "Receba notificações de novos posts nos seus grupos",
                            checked = notifyNewPost,
                            onCheckedChange = { notifyNewPost = it }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        NotificationRow(
                            title = "Convites de Grupos",
                            subtitle = "Receba notificações quando for convidado para um grupo",
                            checked = notifyGroupInvites,
                            onCheckedChange = { notifyGroupInvites = it }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        NotificationRow(
                            title = "Resumo Semanal",
                            subtitle = "Resumo das atividades da sua semana",
                            checked = notifyWeeklyDigest,
                            onCheckedChange = { notifyWeeklyDigest = it }
                        )
                    }

                    ProfileSectionCard(title = "Configurações", icon = Icons.Default.Settings) {
                        SettingsButton(text = "Configurações da Conta", icon = Icons.Default.Settings)
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsButton(text = "Privacidade e Segurança", icon = Icons.Default.Info)
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsButton(text = "Ajuda e Suporte", icon = Icons.Default.Info)
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020)),
                            shape = MaterialTheme.shapes.medium,
                            enabled = logoutState !is LogoutState.Loading
                        ) {
                            if (logoutState is LogoutState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            } else {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sair")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun SettingsButton(text: String, icon: ImageVector) {
    OutlinedButton(
        onClick = { /* TODO */ },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}