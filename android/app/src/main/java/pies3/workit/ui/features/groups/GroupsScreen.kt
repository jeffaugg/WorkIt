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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pies3.workit.ui.features.groups.components.CreateGroupDialog
import pies3.workit.ui.features.groups.components.Group
import pies3.workit.ui.features.groups.components.GroupCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val createGroupState by viewModel.createGroupState.collectAsState()
    val groupsState by viewModel.groupsState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    if (showDialog) {
        CreateGroupDialog(
            onDismiss = {
                if (createGroupState !is CreateGroupUiState.Loading) {
                    showDialog = false
                }
            },
            onCreate = { name, desc, uri ->
                viewModel.createGroup(name, desc, null)
            },
            isLoading = createGroupState is CreateGroupUiState.Loading
        )
    }

    LaunchedEffect(createGroupState) {
        when (createGroupState) {
            is CreateGroupUiState.Success -> {
                val newGroup = (createGroupState as CreateGroupUiState.Success).group
                showDialog = false
                selectedTabIndex = 0
                viewModel.resetCreateGroupState()
                snackbarHostState.showSnackbar(
                    message = "Grupo '${newGroup.name}' criado com sucesso!",
                    duration = SnackbarDuration.Short
                )
            }
            is CreateGroupUiState.Error -> {
                val errorMessage = (createGroupState as CreateGroupUiState.Error).message
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetCreateGroupState()
            }
            else -> { /* Idle ou Loading */ }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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

        when (val state = groupsState) {
            is GroupsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is GroupsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadGroups() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
            is GroupsUiState.Success -> {
                val allGroups = state.groups

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
                    if (allGroups.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (selectedTabIndex == 0)
                                        "Você ainda não está em nenhum grupo"
                                    else
                                        "Nenhum grupo disponível",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(allGroups) { group ->
                            GroupCard(
                                group = Group(
                                    id = group.id,
                                    name = group.name,
                                    description = group.description ?: "",
                                    memberCount = group.users.size,
                                    isAdmin = false, // TODO: verificar se o usuário é admin
                                    imageUrl = group.imageUrl ?: ""
                                ),
                                isMember = selectedTabIndex == 0,
                                onActionClick = {},
                                onCardClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}