package pies3.workit.ui.features.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
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
import pies3.workit.ui.features.profile.components.EditProfileSheet
import pies3.workit.ui.features.profile.components.NotificationRow
import pies3.workit.ui.features.profile.components.ProfileHeader

@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var notifyNewPosts by remember { mutableStateOf(true) }
    var notifyGroupUpdates by remember { mutableStateOf(true) }
    var notifyAchievements by remember { mutableStateOf(true) }
    var notifyWeeklyDigest by remember { mutableStateOf(false) }
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }

    if (isSheetOpen) {
        EditProfileSheet(
            onDismiss = { isSheetOpen = false },
            onSave = { name, email, description, birthDate, photoUri ->
                Log.d("ProfileScreen", "Name: $name, Description: $description, Email: $email, Birth Date: $birthDate, Photo Uri: $photoUri")
                isSheetOpen = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        ProfileHeader(
            name = "Alex Thompson",
            email = "alex.thompson@email.com",
            description = "Corredor, amante dos esportes!",
            memberSince = "Membro desde março de 2024",
            initials = "AT",
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
                text = "Gerencie como você deseja ser notificado sobre atividades em grupo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NotificationRow(
                title = "Novas Postagens",
                subtitle = "Seja notificado quando amigos postarem treinos",
                checked = notifyNewPosts,
                onCheckedChange = { notifyNewPosts = it }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NotificationRow(
                title = "Atualizações do Grupo",
                subtitle = "Novidades sobre seus grupos",
                checked = notifyGroupUpdates,
                onCheckedChange = { notifyGroupUpdates = it }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NotificationRow(
                title = "Conquistas",
                subtitle = "Marcos pessoais e sequências",
                checked = notifyAchievements,
                onCheckedChange = { notifyAchievements = it }
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
                onClick = { /* TODO: Lógica de Logout */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020)),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
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
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
