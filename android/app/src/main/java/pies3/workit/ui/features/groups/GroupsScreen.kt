package pies3.workit.ui.features.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pies3.workit.ui.features.groups.components.Group
import pies3.workit.ui.features.groups.components.GroupCard
import pies3.workit.ui.features.groups.components.CreateGroupDialog

@Composable
fun GroupsScreen() {
    var showDialog by remember { mutableStateOf(false) }
    val myGroups = remember { mutableStateListOf(
        Group(
            id = "1",
            name = "Corredores da Manhã",
            description = "Grupo de corrida matinal para todos os níveis",
            memberCount = 24,
            newPostsCount = 3,
            isAdmin = true
        ),
        Group(
            id = "2",
            name = "Esquadrão da Força",
            description = "Comunidade de levantamento de peso e treino de força",
            memberCount = 31,
            newPostsCount = 5,
            isAdmin = false
        ),
        Group(
            id = "3",
            name = "Fluxo de Yoga",
            description = "Foco em movimento consciente e flexibilidade",
            memberCount = 18,
            newPostsCount = 2,
            isAdmin = false
        ),
        Group(
            id = "1",
            name = "Corredores da Manhã",
            description = "Grupo de corrida matinal para todos os níveis",
            memberCount = 24,
            newPostsCount = 3,
            isAdmin = true
        ),
        Group(
            id = "2",
            name = "Esquadrão da Força",
            description = "Comunidade de levantamento de peso e treino de força",
            memberCount = 31,
            newPostsCount = 5,
            isAdmin = false
        ),
        Group(
            id = "3",
            name = "Fluxo de Yoga",
            description = "Foco em movimento consciente e flexibilidade",
            memberCount = 18,
            newPostsCount = 2,
            isAdmin = false
        ),
        Group(
            id = "4",
            name = "Corredores da Manhã",
            description = "Grupo de corrida matinal para todos os níveis",
            memberCount = 24,
            newPostsCount = 3,
            isAdmin = true
        ),
    ) }

    if (showDialog) {
        CreateGroupDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, desc, url ->
                val newGroup = Group(
                    id = (myGroups.size + 1).toString(),
                    name = name,
                    description = desc,
                    memberCount = 1,
                    newPostsCount = 0,
                    isAdmin = true,
                    imageUrl = url
                )
                myGroups.add(0, newGroup)
                showDialog = false
            }
        )
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Meus Grupos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Criar")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(myGroups) { group ->
                GroupCard(
                    group = group,
                    onLeaveClick = { id ->
                    },
                    onSettingsClick = { id ->
                    }
                )
            }
        }
    }
}