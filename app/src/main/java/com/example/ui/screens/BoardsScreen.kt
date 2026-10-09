package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TattooBoard
import com.example.data.repository.TattooRepository
import com.example.ui.components.MasterSpecDialog
import com.example.ui.components.TattooArtRenderer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BoardsScreen(
    onSelectTattoo: (String) -> Unit,
    onTryOn: (String) -> Unit
) {
    val boards by TattooRepository.boards.collectAsState()
    val savedTryOns by TattooRepository.savedTryOns.collectAsState()
    val allTattoos by TattooRepository.tattoos.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedBoard by remember { mutableStateOf<TattooBoard?>(null) }
    var specTattooId by remember { mutableStateOf<String?>(null) }

    if (specTattooId != null) {
        val tat = allTattoos.find { it.id == specTattooId }
        if (tat != null) {
            MasterSpecDialog(
                tattoo = tat,
                onDismiss = { specTattooId = null }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedBoard != null) {
                IconButton(onClick = { selectedBoard = null }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад к доскам",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            Column {
                Text(
                    text = selectedBoard?.title ?: "Мои коллекции & Примерки",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = selectedBoard?.description ?: "Сохраненные эскизы и история примерки на теле",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (selectedBoard == null) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Доски (${boards.size})", fontSize = 13.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Примерки (${savedTryOns.size})", fontSize = 13.sp)
                        }
                    }
                )
            }
        }

        // Tab Content
        if (selectedBoard != null) {
            // View Tattoos inside selected board
            val boardTattoos = allTattoos.filter { selectedBoard!!.tattooIds.contains(it.id) }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(boardTattoos) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectTattoo(item.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF222530), Color(0xFF14161E))
                                        )
                                    )
                                    .padding(12.dp)
                            ) {
                                TattooArtRenderer(
                                    patternType = item.artPatternType,
                                    modifier = Modifier.fillMaxSize(),
                                    inkColor = Color(0xFFF0F1F5),
                                    accentColor = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = item.targetBodyPart.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else if (selectedTabIndex == 0) {
            // Boards List Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().testTag("boards_grid")
            ) {
                items(boards) { board ->
                    val coverItem = allTattoos.find { it.id == board.coverTattooId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { selectedBoard = board },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.2f)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF242733), Color(0xFF13151D))
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                TattooArtRenderer(
                                    patternType = coverItem?.artPatternType ?: "CYBER_DRAGON",
                                    modifier = Modifier.fillMaxSize(),
                                    inkColor = Color(0xFFF0F1F5),
                                    accentColor = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = board.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${board.tattooIds.size} эскизов",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Try-On History List
            if (savedTryOns.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Вы еще не сохраняли примерки на теле.\nВыберите любой эскиз в ленте и нажмите «Примерить»!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().testTag("try_on_history_list")
                ) {
                    items(savedTryOns) { item ->
                        val tat = allTattoos.find { it.id == item.tattooId }
                        val dateFormatted = remember(item.timestamp) {
                            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            sdf.format(Date(item.timestamp))
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1D202A))
                                        .padding(8.dp)
                                ) {
                                    TattooArtRenderer(
                                        patternType = tat?.artPatternType ?: "CYBER_DRAGON",
                                        modifier = Modifier.fillMaxSize(),
                                        inkColor = Color.White,
                                        accentColor = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tat?.title ?: "Эскиз",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Зона: ${item.bodyZone.displayName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = dateFormatted,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    IconButton(onClick = { specTattooId = tat?.id }) {
                                        Icon(
                                            imageVector = Icons.Default.Assignment,
                                            contentDescription = "ТЗ мастеру",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { if (tat != null) onTryOn(tat.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.CenterFocusStrong,
                                            contentDescription = "Открыть примерку",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
