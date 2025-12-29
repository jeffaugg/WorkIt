package pies3.workit.ui.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pies3.workit.ui.features.profile.components.NotificationRow
import pies3.workit.ui.features.profile.components.ProfileHeader
import pies3.workit.ui.features.profile.components.StatCard

@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var notifyNewPosts by remember { mutableStateOf(true) }
    var notifyGroupUpdates by remember { mutableStateOf(true) }
    var notifyAchievements by remember { mutableStateOf(true) }
    var notifyWeeklyDigest by remember { mutableStateOf(false) }

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
            memberSince = "Membro desde março de 2024",
            initials = "AT",
            onEditClick = { /* TODO */ }
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    value = "89",
                    label = "Treinos Totais",
                    icon = Icons.Default.Star,
                    iconColor = Color(0xFFFFD700),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "12",
                    label = "Semanas Seguidas",
                    icon = Icons.Default.DateRange,
                    iconColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    value = "Corrida",
                    label = "Atividade Favorita",
                    icon = Icons.Default.Check,
                    iconColor = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "3",
                    label = "Grupos Participando",
                    icon = Icons.Default.AccountBox,
                    iconColor = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        ProfileSectionCard(title = "Aparência", icon = Icons.Outlined.Build) {
            Text(
                text = "Personalize a aparência do aplicativo",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
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
                color = Color.Gray,
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
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = Color.Black)
        }
    }
}