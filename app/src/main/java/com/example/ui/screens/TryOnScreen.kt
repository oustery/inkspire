package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ai.AiPlacementFeedback
import com.example.ai.GeminiTattooService
import com.example.data.model.BodyZone
import com.example.data.repository.TattooRepository
import com.example.ui.components.BodyTemplateCanvas
import com.example.ui.components.MasterSpecDialog
import com.example.ui.components.TattooArtRenderer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TryOnScreen(
    tattooId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tattoos by TattooRepository.tattoos.collectAsState()
    val tattoo = tattoos.find { it.id == tattooId }

    if (tattoo == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Эскиз не найден")
        }
        return
    }

    // Selected Zone state
    var selectedZone by remember { mutableStateOf(tattoo.targetBodyPart) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Шаблоны тела, 1: Свое фото
    var userPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Canvas Transformation State (Pinch, Rotate, Drag)
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1.0f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var opacity by remember { mutableFloatStateOf(0.88f) }
    var isFlippedHorizontally by remember { mutableStateOf(false) }
    var isPreviewVisible by remember { mutableStateOf(true) } // For Before/After comparison

    // Blend mode (Multiply for realistic skin ink embedding, Darken, or Normal)
    var selectedBlendMode by remember { mutableStateOf(BlendMode.Multiply) }

    // AI Analysis State
    var isAiAnalyzing by remember { mutableStateOf(false) }
    var aiFeedback by remember { mutableStateOf<AiPlacementFeedback?>(null) }
    var showAiSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Master Spec Sheet
    var showSpecDialog by remember { mutableStateOf(false) }

    // Zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            userPhotoUri = uri
            selectedTab = 1
        }
    }

    if (showSpecDialog) {
        MasterSpecDialog(
            tattoo = tattoo.copy(targetBodyPart = selectedZone),
            onDismiss = { showSpecDialog = false }
        )
    }

    // AI Analysis Bottom Sheet
    if (showAiSheet && aiFeedback != null) {
        ModalBottomSheet(
            onDismissRequest = { showAiSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Анализ посадки эскиза",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${tattoo.title} • ${selectedZone.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${aiFeedback!!.harmonyScore}% Совместимость",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                AiFeedbackCard(
                    title = "Анатомическая гармония",
                    content = aiFeedback!!.anatomicalAdvice,
                    iconTint = MaterialTheme.colorScheme.primary
                )

                AiFeedbackCard(
                    title = "Карта ощущений и чувствительности",
                    content = aiFeedback!!.painDetails,
                    iconTint = MaterialTheme.colorScheme.secondary
                )

                AiFeedbackCard(
                    title = "Долговечность и заживление",
                    content = aiFeedback!!.durabilityNotes,
                    iconTint = MaterialTheme.colorScheme.tertiary
                )

                AiFeedbackCard(
                    title = "Совет топ-мастера",
                    content = aiFeedback!!.artistRecommendation,
                    iconTint = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = { showAiSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Понятно, продолжить примерку", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("try_on_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AI Body Try-On",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = tattoo.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Save Try-On Action
            IconButton(
                onClick = {
                    TattooRepository.saveTryOn(
                        tattooId = tattoo.id,
                        zone = selectedZone,
                        scale = scale,
                        rotation = rotation
                    )
                    Toast.makeText(context, "Примерка сохранена в профиль!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("save_try_on_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Сохранить",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Source Switcher (Шаблоны vs Свое фото)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Анатомические шаблоны", fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    if (userPhotoUri == null) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    } else {
                        selectedTab = 1
                    }
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                        Text(if (userPhotoUri != null) "Мое фото" else "Загрузить фото", fontSize = 13.sp)
                    }
                }
            )
        }

        // Zone Selector Chips (shown when template tab is active)
        if (selectedTab == 0) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val availableZones = listOf(
                    BodyZone.FOREARM,
                    BodyZone.WRIST,
                    BodyZone.COLLARBONE,
                    BodyZone.SHOULDER,
                    BodyZone.RIBS,
                    BodyZone.THIGH,
                    BodyZone.BACK
                )
                items(availableZones) { zone ->
                    val isSelected = selectedZone == zone
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedZone = zone },
                        label = { Text(zone.displayName, fontSize = 11.sp) },
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
        }

        // Interactive AR Canvas & Editor Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                .testTag("try_on_canvas_stage")
        ) {
            // Layer 1: Body Background (Template or Photo)
            if (selectedTab == 1 && userPhotoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = userPhotoUri),
                    contentDescription = "Фото тела",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                BodyTemplateCanvas(
                    zone = selectedZone,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Layer 2: Tattoo Overlay with Multi-touch Transformations
            if (isPreviewVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotationChange ->
                                offsetX += pan.x
                                offsetY += pan.y
                                scale = (scale * zoom).coerceIn(0.25f, 3.5f)
                                rotation += rotationChange
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .graphicsLayer {
                                translationX = offsetX
                                translationY = offsetY
                                scaleX = if (isFlippedHorizontally) -scale else scale
                                scaleY = scale
                                rotationZ = rotation
                            }
                    ) {
                        TattooArtRenderer(
                            patternType = tattoo.artPatternType,
                            modifier = Modifier.fillMaxSize(),
                            inkColor = Color(0xFF161517),
                            accentColor = Color(0xFFE5B54F),
                            blendMode = selectedBlendMode,
                            alpha = opacity
                        )
                    }
                }
            }

            // Floating Controls On The Canvas Stage (Top-right & Top-left)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Before/After Toggle
                Card(
                    onClick = { isPreviewVisible = !isPreviewVisible },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPreviewVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "До / После",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPreviewVisible) "Скрыть тату" else "Показать", fontSize = 11.sp, color = Color.White)
                    }
                }

                // Reset & Flip Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { isFlippedHorizontally = !isFlippedHorizontally },
                        modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = "Отразить",
                            tint = if (isFlippedHorizontally) MaterialTheme.colorScheme.primary else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            offsetX = 0f
                            offsetY = 0f
                            scale = 1.0f
                            rotation = 0f
                        },
                        modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Сбросить позицию",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Gestures Hint Overlay (Bottom of canvas)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Двигайте 1 пальцем • Масштабируйте и вращайте 2 пальцами",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
        }

        // Controls Dock & Sliders
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Opacity & Blend Mode Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Прозрачность пигмента: ${(opacity * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Blend mode toggle (Multiply vs Normal)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (selectedBlendMode == BlendMode.Multiply) "Эффект кожи (Multiply)" else "Трафарет",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(6.dp)
                            ).padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Slider(
                    value = opacity,
                    onValueChange = { opacity = it },
                    valueRange = 0.2f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(20.dp)
                )

                // Approach 2: AI Analysis & Master Spec Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // AI Analysis Button
                    Button(
                        onClick = {
                            isAiAnalyzing = true
                            scope.launch {
                                val result = GeminiTattooService.analyzeBodyPlacement(
                                    tattooTitle = tattoo.title,
                                    style = tattoo.style,
                                    zone = selectedZone,
                                    sizeCm = tattoo.defaultSizeCm
                                )
                                aiFeedback = result
                                isAiAnalyzing = false
                                showAiSheet = true
                            }
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("trigger_ai_placement_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isAiAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).padding(end = 4.dp)
                            )
                            Text("ИИ Анализ посадки", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Master Spec Sheet Button
                    OutlinedButton(
                        onClick = { showSpecDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("open_spec_from_tryon_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                        Text("ТЗ мастеру", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
fun AiFeedbackCard(
    title: String,
    content: String,
    iconTint: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = iconTint
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}
