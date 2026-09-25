package com.example.tugaspraktikumpam2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugaspraktikumpam2.model.NewsDisplay
import com.example.tugaspraktikumpam2.repository.NewsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val repository = remember { NewsRepository() }

    // StateFlow collect untuk jumlah berita terbaca (Requirement StateFlow)
    val readCount by repository.readCount.collectAsState()

    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Teknologi", "Olahraga", "Edukasi")

    var newsFeed by remember { mutableStateOf<List<NewsDisplay>>(emptyList()) }
    var latestLog by remember { mutableStateOf("Menunggu data berita...") }

    var selectedDetailText by remember { mutableStateOf<String?>(null) }
    var isLoadingDetail by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Flow collector untuk menerima streaming berita setiap 2 detik (Requirement Flow & Operators)
    LaunchedEffect(selectedCategory) {
        newsFeed = emptyList()
        repository.getFilteredAndTransformedNewsStream(
            selectedCategory = selectedCategory,
            onLogAction = { log ->
                latestLog = log
            }
        ).collect { newNewsDisplay ->
            // Collect terminal operator
            newsFeed = listOf(newNewsDisplay) + newsFeed
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("News Feed Simulator", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (selectedDetailText != null) {
                                    selectedDetailText = null
                                } else if (selectedCategory != "Semua") {
                                    selectedCategory = "Semua"
                                }
                            }
                        ) {
                            Text("←", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Info Box: StateFlow Read Count & Log onEach
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Jumlah Berita Dibaca: $readCount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aktivitas Berita Masuk: $latestLog",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Category
                Text(
                    text = "Filter Kategori:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Feed Berita (Masuk Tiap 2 Detik):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // News Feed List
                if (newsFeed.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Mengambil berita ($selectedCategory)...", fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(newsFeed, key = { "${it.id}-${it.formattedTime}" }) { news ->
                            NewsCard(
                                news = news,
                                onReadClick = {
                                    // Coroutine async/await
                                    coroutineScope.launch {
                                        isLoadingDetail = true
                                        val detail = repository.fetchNewsDetailAsync(news)
                                        repository.markAsRead(news.id)
                                        isLoadingDetail = false
                                        selectedDetailText = detail
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dialog Detail Berita
        if (isLoadingDetail) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Memuat Detail Berita...") },
                text = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                confirmButton = {}
            )
        } else if (selectedDetailText != null) {
            AlertDialog(
                onDismissRequest = { selectedDetailText = null },
                title = { Text("Detail Berita") },
                text = { Text(selectedDetailText ?: "") },
                confirmButton = {
                    Button(onClick = { selectedDetailText = null }) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

@Composable
fun NewsCard(
    news: NewsDisplay,
    onReadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = news.formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = if (news.isRead) "✓ Sudah Dibaca" else "• Belum Dibaca",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (news.isRead) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.formattedTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onReadClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Baca Detail", fontSize = 12.sp)
                }
            }
        }
    }
}
