package pies3.workit.ui.features.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pies3.workit.ui.features.groups.components.CreateGroupDialog
import pies3.workit.ui.features.groups.components.Group
import pies3.workit.ui.features.groups.components.GroupCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val myGroups = remember { mutableStateListOf(
        Group(id = "1", name = "Corredores da Manhã", description = "Foco em maratonas e 5k.", memberCount = 24, isAdmin = true),
        Group(id = "2", name = "Esquadrão da Força", description = "Powerlifting e força.", memberCount = 31, isAdmin = false)
    )}


    val discoverGroups = remember { mutableStateListOf(
        Group(id = "3", name = "Fluxo de Yoga", description = "Movimento consciente.", memberCount = 18, isAdmin = false),
        Group(id = "4", name = "Calistenia Urbana", description = "Treino de rua e barras.", memberCount = 42, isAdmin = false),
        Group(id = "5", name = "Clube do Pedal", description = "Ciclismo de estrada.", memberCount = 150, isAdmin = false)
    )}

    if (showDialog) {
        CreateGroupDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, desc, uri ->
                myGroups.add(0, Group(id = "new", name = name, description = desc, memberCount = 1, isAdmin = true, imageUrl = uri?.toString() ?: ""))
                showDialog = false
                selectedTabIndex = 0
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Comunidade", fontWeight = FontWeight.Bold) },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    )
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Meus Grupos") },
                        icon = { Icon(Icons.Default.Group, null) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Explorar") },
                        icon = { Icon(Icons.Default.Explore, null) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Criar Grupo")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        val currentList = if (selectedTabIndex == 0) myGroups else discoverGroups
        val isMyGroupsTab = selectedTabIndex == 0

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            items(currentList) { group ->
                GroupCard(
                    group = group,
                    isMember = isMyGroupsTab,
                    onActionClick = {
                    },
                    onCardClick = {
                    }
                )
            }
        }
    }
}