package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BodyZone
import com.example.data.model.TattooItem
import com.example.data.model.TattooStyle
import com.example.data.repository.TattooRepository
import com.example.ui.components.TattooArtRenderer

@Composable
fun FeedScreen(
    onSelectTattoo: (String) -> Unit,
    onTryOnTattoo: (String) -> Unit,
    onOpenAiLab: () -> Unit
) {
    val tattoos by TattooRepository.tattoos.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(TattooStyle.ALL) }
    var selectedZone by remember { mutableStateOf(BodyZone.ALL) }

    val filteredTattoos = remember(tattoos, searchQuery, selectedStyle, selectedZone) {
        tattoos.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.artist.contains(searchQuery, ignoreCase = true) ||
                    item.tags.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesStyle = selectedStyle == TattooStyle.ALL || item.style == selectedStyle
            val matchesZone = selectedZone == BodyZone.ALL || item.targetBodyPart == selectedZone

            matchesSearch && matchesStyle && matchesZone
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header & Branding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ink",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "spire",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Идеи тату & ИИ Примерка",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick AI Generator Button
            Card(
                onClick = onOpenAiLab,
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("open_ai_lab_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Studio",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "AI Эскиз",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Поиск: стиль, дракон, цветы, змея...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("feed_search_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Style Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(TattooStyle.values()) { style ->
                val selected = selectedStyle == style
                FilterChip(
                    selected = selected,
                    onClick = { selectedStyle = style },
                    label = { Text(style.chipName, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Horizontal Body Zone Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)
        ) {
            items(BodyZone.values()) { zone ->
                val selected = selectedZone == zone
                FilterChip(
                    selected = selected,
                    onClick = { selectedZone = zone },
                    label = { Text(zone.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Pinterest Staggered / Masonry Grid
        if (filteredTattoos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Эскизов не найдено. Попробуйте другой запрос или создайте новый с помощью AI!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 12.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("pinterest_masonry_grid")
            ) {
                items(filteredTattoos, key = { it.id }) { item ->
                    PinterestTattooCard(
                        tattoo = item,
                        onClick = { onSelectTattoo(item.id) },
                        onTryOn = { onTryOnTattoo(item.id) },
                        onToggleLike = { TattooRepository.toggleLike(item.id) },
                        onToggleSave = { TattooRepository.toggleSave(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PinterestTattooCard(
    tattoo: TattooItem,
    onClick: () -> Unit,
    onTryOn: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("tattoo_card_${tattoo.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Visual Artwork Canvas Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(tattoo.aspectRatio)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF222530), Color(0xFF14161E))
                        )
                    )
                    .padding(14.dp)
            ) {
                TattooArtRenderer(
                    patternType = tattoo.artPatternType,
                    modifier = Modifier.fillMaxSize(),
                    inkColor = Color(0xFFF0F1F5),
                    accentColor = MaterialTheme.colorScheme.primary
                )

                // Top Floating Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Style pill
                    Box(
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.65f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tattoo.style.chipName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Save / Bookmark action
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (tattoo.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Сохранить",
                            tint = if (tattoo.isSaved) MaterialTheme.colorScheme.primary else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Quick "Примерить" floating button on bottom right of the image
                Card(
                    onClick = onTryOn,
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .testTag("quick_try_on_${tattoo.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "Примерить",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Примерить",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Card Details & Social Metrics
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = tattoo.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${tattoo.artist} • ${tattoo.targetBodyPart.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Approximate pain level pill
                    Text(
                        text = "Боль: ${tattoo.painLevel}/10",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            tattoo.painLevel <= 3 -> Color(0xFF10B981)
                            tattoo.painLevel <= 6 -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        },
                        fontWeight = FontWeight.SemiBold
                    )

                    // Likes Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onToggleLike)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Лайк",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "${tattoo.likesCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
