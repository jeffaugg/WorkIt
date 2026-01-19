package pies3.workit.ui.features.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pies3.workit.ui.features.groups.components.CreateGroupDialog
import pies3.workit.ui.features.groups.components.Group
import pies3.workit.ui.features.groups.components.GroupCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel = hiltViewModel(),
    onGroupClick: (String, String) -> Unit = { _, _ -> }
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val createGroupState by viewModel.createGroupState.collectAsState()
    val myGroupsState by viewModel.myGroupsState.collectAsState()
    val allGroupsState by viewModel.allGroupsState.collectAsState()
    val joinGroupState by viewModel.joinGroupState.collectAsState()
    val leaveGroupState by viewModel.leaveGroupState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val myGroupsSearchQuery by viewModel.myGroupsSearchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val currentGroupsState = if (selectedTabIndex == 0) myGroupsState else allGroupsState

    if (showDialog) {
        CreateGroupDialog(
            onDismiss = { showDialog = false },
            isLoading = createGroupState is CreateGroupUiState.Loading,
            onCreate = { name, description, uri ->
                viewModel.createGroupWithImageUpload(name, description, uri)
            }
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

    LaunchedEffect(joinGroupState) {
        when (joinGroupState) {
            is JoinGroupUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Você entrou no grupo com sucesso!",
                    duration = SnackbarDuration.Short
                )
                viewModel.resetJoinGroupState()
            }
            is JoinGroupUiState.Error -> {
                val errorMessage = (joinGroupState as JoinGroupUiState.Error).message
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetJoinGroupState()
            }
            else -> { /* Idle ou Loading */ }
        }
    }

    LaunchedEffect(leaveGroupState) {
        when (leaveGroupState) {
            is LeaveGroupUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Você saiu do grupo com sucesso!",
                    duration = SnackbarDuration.Short
                )
                viewModel.resetLeaveGroupState()
            }
            is LeaveGroupUiState.Error -> {
                val errorMessage = (leaveGroupState as LeaveGroupUiState.Error).message
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetLeaveGroupState()
            }
            else -> {}
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

                val keyboardController = LocalSoftwareKeyboardController.current
                OutlinedTextField(
                    value = if (selectedTabIndex == 0) myGroupsSearchQuery else searchQuery,
                    onValueChange = {
                        if (selectedTabIndex == 0) {
                            viewModel.updateMyGroupsSearchQuery(it)
                        } else {
                            viewModel.updateSearchQuery(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar grupos...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        val currentQuery = if (selectedTabIndex == 0) myGroupsSearchQuery else searchQuery
                        if (currentQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                if (selectedTabIndex == 0) {
                                    viewModel.updateMyGroupsSearchQuery("")
                                } else {
                                    viewModel.updateSearchQuery("")
                                }
                            }) {
                                Icon(Icons.Default.Close, "Limpar busca")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    )
                )
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

        when (val state = currentGroupsState) {
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
                        Button(onClick = {
                            if (selectedTabIndex == 0) {
                                viewModel.loadMyGroups()
                            } else {
                                viewModel.loadExploreGroups()
                            }
                        }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
            is GroupsUiState.Success -> {
                val groups = state.groups
                val isMember = selectedTabIndex == 0
                val isJoining = joinGroupState is JoinGroupUiState.Loading
                val isLeaving = leaveGroupState is LeaveGroupUiState.Loading

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                ) {
                    if (groups.isEmpty()) {
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
                        items(groups) { group ->
                            GroupCard(
                                group = Group(
                                    id = group.id,
                                    name = group.name,
                                    description = group.description ?: "",
                                    memberCount = group.users.size,
                                    isAdmin = false,
                                    imageUrl = group.imageUrl ?: ""
                                ),
                                isMember = isMember,
                                onActionClick = {
                                    if (isMember) {
                                        viewModel.leaveGroup(group.id)
                                    } else {
                                        viewModel.joinGroup(group.id)
                                    }
                                },
                                onCardClick = { onGroupClick(group.id, group.name) },
                                isLoading = isJoining || isLeaving
                            )
                        }
                    }
                }
            }
        }
    }
}