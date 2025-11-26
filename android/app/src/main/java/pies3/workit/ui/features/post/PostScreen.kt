package pies3.workit.ui.features.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pies3.workit.ui.features.post.components.FormDropdown
import pies3.workit.ui.features.post.components.MediaButton
import pies3.workit.ui.features.post.components.TipItem

@Composable
fun PostScreen() {
    var selectedGroup by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val groups = listOf("Corredores da Manhã", "Esquadrão de Força", "Yoga Flow")
    val activities = listOf("Corrida", "Levantamento de Peso", "Yoga", "Ciclismo", "HIIT")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Compartilhe seu Treino",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface // Branco
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Postar Atividade",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Compartilhe seu treino com seus grupos fitness e inspire outros!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormDropdown(
                    label = "Grupo *",
                    options = groups,
                    selectedOption = selectedGroup,
                    onOptionSelected = { selectedGroup = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormDropdown(
                    label = "Tipo de Atividade *",
                    options = activities,
                    selectedOption = activityType,
                    onOptionSelected = { activityType = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Localização") },
                    placeholder = { Text("Onde você treinou?") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    modifier = Modifier.fillMaxWidth()
                    // Cores padrão funcionam bem no fundo branco
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição *") },
                    placeholder = { Text("Conte-nos sobre seu treino! Como foi?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MediaButton(
                        text = "Fotos (0/3)",
                        icon = Icons.Default.Star,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    MediaButton(
                        text = "Vídeo (10-15s)",
                        icon = Icons.Default.PlayArrow,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Compartilhar Atividade", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Dicas para Ótimos Posts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        TipItem("1", "Mantenha sua descrição positiva e encorajadora.")
        TipItem("2", "Inclua recordes pessoais ou conquistas.")
        TipItem("3", "Adicione fotos que mostrem seu ambiente de treino.")

        Spacer(modifier = Modifier.height(32.dp))
    }
}