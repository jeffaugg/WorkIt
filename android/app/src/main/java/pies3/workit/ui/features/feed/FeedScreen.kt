package pies3.workit.ui.features.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ActivityTypeUI {
    RUNNING, WALKING, CYCLING, WEIGHT_TRAINING, SWIMMING, OTHER
}

data class PostUI(
    val id: String,
    val title: String,
    val activityType: ActivityTypeUI,
    val body: String?,
    val imageUrl: String?,
    val location: String?,
    val timeAgo: String,
    val userName: String,
    val userAvatarUrl: String?,
    val groupName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    var posts by remember {
        mutableStateOf(
            listOf(
                PostUI(
                    id = "1",
                    title = "Corrida no Parque",
                    activityType = ActivityTypeUI.RUNNING,
                    body = "Hoje o ritmo foi intenso! Consegui baixar meu tempo em 30 segundos. O visual ajudou demais.",
                    imageUrl = "https://images.unsplash.com/photo-1552674605-db6ffd4facb5?w=500&auto=format&fit=crop&q=60",
                    location = "Parque da Cidade",
                    timeAgo = "2h atrás",
                    userName = "Ana Silva",
                    userAvatarUrl = "https://images.unsplash.com/photo-1621398944996-b8e0d40fca71?w=500&auto=format&fit=crop&q=60",
                    groupName = "Corredores da Manhã"
                ),
                PostUI(
                    id = "2",
                    title = "Leg Day Pesado",
                    activityType = ActivityTypeUI.WEIGHT_TRAINING,
                    body = "Foco total na execução hoje. Agachamento com 100kg batido!",
                    imageUrl = "https://plus.unsplash.com/premium_photo-1676634832558-6654a134e920?q=80&w=1471&auto=format&fit=crop",
                    location = "Academia Ironberg",
                    timeAgo = "5h atrás",
                    userName = "Carlos Souza",
                    userAvatarUrl = "https://images.unsplash.com/photo-1625262550495-1d3bfb5c1502?w=500&auto=format&fit=crop&q=60",
                    groupName = "Esquadrão da Força"
                ),
                PostUI(
                    id = "3",
                    title = "Trilha de Domingo",
                    activityType = ActivityTypeUI.CYCLING,
                    body = "A lama faz parte da diversão! Trilha técnica hoje.",
                    imageUrl = "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=500&auto=format&fit=crop&q=60",
                    location = "Serra do Mar",
                    timeAgo = "Ontem",
                    userName = "Marina Costa",
                    userAvatarUrl = "https://images.unsplash.com/photo-1625012612550-622fe6a78a05?w=500&auto=format&fit=crop&q=60",
                    groupName = "Clube do Pedal"
                ),
                PostUI(
                    id = "4",
                    title = "Futebol com a Galera",
                    activityType = ActivityTypeUI.OTHER,
                    body = "Nada como um jogo no fim de semana para relaxar e suar a camisa.",
                    imageUrl = "https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=500&auto=format&fit=crop&q=60",
                    location = "Arena Society",
                    timeAgo = "1d atrás",
                    userName = "Lucas Pereira",
                    userAvatarUrl = null,
                    groupName = "Futebol de Quarta"
                ),
                PostUI(
                    id = "5",
                    title = "Treino de Tiro",
                    activityType = ActivityTypeUI.RUNNING,
                    body = "Focando na velocidade hoje. 10x 400m.",
                    imageUrl = "https://images.unsplash.com/photo-1617085606193-6b17105cff2a?w=500&auto=format&fit=crop&q=60",
                    location = "Pista de Atletismo",
                    timeAgo = "2d atrás",
                    userName = "Fernanda Lima",
                    userAvatarUrl = "https://images.unsplash.com/photo-1621398944996-b8e0d40fca71?w=500&auto=format&fit=crop&q=60",
                    groupName = "Atletismo Pro"
                ),
                PostUI(
                    id = "6",
                    title = "Nascer do Sol na Estrada",
                    activityType = ActivityTypeUI.CYCLING,
                    body = "Acordar cedo tem suas recompensas.",
                    imageUrl = "https://images.unsplash.com/photo-1444491741275-3747c53c99b4?w=500&auto=format&fit=crop&q=60",
                    location = "Rodovia dos Bandeirantes",
                    timeAgo = "3d atrás",
                    userName = "Roberto Alves",
                    userAvatarUrl = "https://images.unsplash.com/photo-1625262550495-1d3bfb5c1502?w=500&auto=format&fit=crop&q=60",
                    groupName = "Speed Cycling"
                )
            )
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "WorkIt Feed",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    delay(2000)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (posts.isEmpty()) {
                EmptyFeedState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(posts) { post ->
                        PostCard(post = post)
                    }
                    item {
                        EndOfFeedIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(post: PostUI) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = post.userAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                    placeholder = null
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.groupName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Text(
                            text = " • ${post.timeAgo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                ActivityBadge(post.activityType)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!post.location.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!post.body.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (post.imageUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun EmptyFeedState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Feed,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Seu feed está vazio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Entre em grupos ou siga atividades para ver as postagens aqui.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { }) {
            Text("Explorar Grupos")
        }
    }
}

@Composable
fun EndOfFeedIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Você viu tudo por enquanto!",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActivityBadge(type: ActivityTypeUI) {
    val (icon, color) = when (type) {
        ActivityTypeUI.RUNNING -> Icons.AutoMirrored.Filled.DirectionsRun to Color(0xFFFF9800)
        ActivityTypeUI.WALKING -> Icons.AutoMirrored.Filled.DirectionsWalk to Color(0xFF8BC34A)
        ActivityTypeUI.CYCLING -> Icons.AutoMirrored.Filled.DirectionsBike to Color(0xFF03A9F4)
        ActivityTypeUI.WEIGHT_TRAINING -> Icons.Default.FitnessCenter to Color(0xFFF44336)
        ActivityTypeUI.SWIMMING -> Icons.Default.Pool to Color(0xFF00BCD4)
        ActivityTypeUI.OTHER -> Icons.Default.Sports to Color(0xFF9E9E9E)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = getActivityLabel(type),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

fun getActivityLabel(type: ActivityTypeUI): String {
    return when (type) {
        ActivityTypeUI.RUNNING -> "Corrida"
        ActivityTypeUI.WALKING -> "Caminhada"
        ActivityTypeUI.CYCLING -> "Ciclismo"
        ActivityTypeUI.WEIGHT_TRAINING -> "Musculação"
        ActivityTypeUI.SWIMMING -> "Natação"
        ActivityTypeUI.OTHER -> "Outro"
    }
}
