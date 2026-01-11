package pies3.workit.ui.features.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pies3.workit.ui.features.groups.GroupsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    postId: String,
    onNavigateBack: () -> Unit,
    onPostUpdated: () -> Unit = {},
    editPostViewModel: EditPostViewModel = hiltViewModel(),
    groupsViewModel: GroupsViewModel = hiltViewModel()
) {
    val postState by editPostViewModel.postState.collectAsStateWithLifecycle()
    val updateState by editPostViewModel.updateState.collectAsStateWithLifecycle()
    val myGroupsState by groupsViewModel.myGroupsState.collectAsStateWithLifecycle()

    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var selectedGroupName by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val activityTypeMap = mapOf(
        "Corrida" to "RUNNING",
        "Caminhada" to "WALKING",
        "Ciclismo" to "CYCLING",
        "Levantamento de Peso" to "WEIGHT_TRAINING",
        "Natação" to "SWIMMING",
        "Yoga" to "OTHER",
        "HIIT" to "OTHER",
        "Boxe" to "OTHER",
        "CrossFit" to "OTHER"
    )

    val activityTypeReverseMap = mapOf(
        "RUNNING" to "Corrida",
        "WALKING" to "Caminhada",
        "CYCLING" to "Ciclismo",
        "WEIGHT_TRAINING" to "Levantamento de Peso",
        "SWIMMING" to "Natação",
        "OTHER" to "Yoga"
    )

    val activities = listOf("Corrida", "Caminhada", "Ciclismo", "Levantamento de Peso", "Natação", "Yoga", "HIIT", "Boxe", "CrossFit")

    LaunchedEffect(postId) {
        editPostViewModel.loadPost(postId)
    }

    LaunchedEffect(postState) {
        if (postState is PostDetailState.Success) {
            val post = (postState as PostDetailState.Success).post
            title = post.title
            activityType = activityTypeReverseMap[post.activityType] ?: "Yoga"
            description = post.body ?: ""
            location = post.location ?: ""
            selectedGroup = post.group.id
            selectedGroupName = post.group.name
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is UpdatePostUiState.Success) {
            editPostViewModel.resetState()
            onPostUpdated()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar Publicação", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (postState) {
            is PostDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PostDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (postState as PostDetailState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is PostDetailState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))

                        when (val state = myGroupsState) {
                            is pies3.workit.ui.features.groups.GroupsUiState.Success -> {
                                val groupOptions = state.groups.map { it.name }
                                val groupIdMap = state.groups.associate { it.name to it.id }

                                ModernDropdown(
                                    label = "Selecionar Grupo",
                                    options = groupOptions,
                                    selectedOption = selectedGroupName,
                                    onOptionSelected = { name ->
                                        selectedGroupName = name
                                        selectedGroup = groupIdMap[name]
                                    },
                                    icon = Icons.Default.Groups
                                )
                            }
                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ModernDropdown(
                            label = "Tipo de Atividade",
                            options = activities,
                            selectedOption = activityType,
                            onOptionSelected = { activityType = it },
                            icon = Icons.Default.FitnessCenter
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descrição") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            maxLines = 6,
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (updateState is UpdatePostUiState.Error) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = (updateState as UpdatePostUiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                selectedGroup?.let { groupId ->
                                    val mappedActivityType = activityTypeMap[activityType] ?: "OTHER"
                                    editPostViewModel.updatePost(
                                        postId = postId,
                                        title = title,
                                        activityType = mappedActivityType,
                                        body = description.ifBlank { null },
                                        imageUrl = null,
                                        location = location.ifBlank { null },
                                        groupId = groupId
                                    )
                                }
                            },
                            enabled = updateState !is UpdatePostUiState.Loading &&
                                    activityType.isNotBlank() &&
                                    title.isNotBlank() &&
                                    selectedGroup != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (updateState is UpdatePostUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    "Salvar Alterações",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
