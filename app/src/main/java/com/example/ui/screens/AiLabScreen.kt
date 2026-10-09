package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiConceptResult
import com.example.ai.GeminiTattooService
import com.example.data.model.BodyZone
import com.example.data.model.TattooStyle
import com.example.data.repository.TattooRepository
import com.example.ui.components.TattooArtRenderer
import kotlinx.coroutines.launch

@Composable
fun AiLabScreen(
    onBack: () -> Unit,
    onTryOnGenerated: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(TattooStyle.CYBER_SIGILISM) }
    var selectedZone by remember { mutableStateOf(BodyZone.FOREARM) }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<AiConceptResult?>(null) }
    var generatedTattooId by remember { mutableStateOf<String?>(null) }

    val presetPrompts = listOf(
        "Кибернетический феникс в тонких линиях",
        "Лунная змея, обвивающая кристалл",
        "Сакральный лотос с фрактальной геометрией",
        "Японский карп кои с брызгами туши",
        "Черная пантера с олдскульной розой"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("ai_lab_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "AI Tattoo Generator Studio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Intro Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column {
                        Text(
                            text = "Генератор авторских эскизов",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Опишите любую идею, а ИИ создаст профессиональный эскиз с ТЗ для трансфера и примерки на теле.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Prompt Input Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Опишите вашу идею татуировки:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Например: Дракон в стиле кибер-сигил с шипами...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("ai_prompt_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Quick Preset Prompts
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Быстрые идеи для вдохновения:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presetPrompts) { p ->
                        SuggestionChip(
                            onClick = { prompt = p },
                            label = { Text(p, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Style Selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Выберите тату-стиль:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val styles = listOf(
                        TattooStyle.CYBER_SIGILISM,
                        TattooStyle.FINE_LINE,
                        TattooStyle.MINIMALISM,
                        TattooStyle.JAPANESE,
                        TattooStyle.NEO_TRADITIONAL,
                        TattooStyle.GEOMETRY,
                        TattooStyle.BLACKWORK
                    )
                    items(styles) { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            label = { Text(style.displayName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Body Zone Selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Желаемое место нанесения:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val zones = listOf(
                        BodyZone.FOREARM,
                        BodyZone.COLLARBONE,
                        BodyZone.WRIST,
                        BodyZone.SHOULDER,
                        BodyZone.RIBS,
                        BodyZone.THIGH,
                        BodyZone.BACK
                    )
                    items(zones) { zone ->
                        FilterChip(
                            selected = selectedZone == zone,
                            onClick = { selectedZone = zone },
                            label = { Text(zone.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Generate Button
            Button(
                onClick = {
                    if (prompt.isBlank()) {
                        prompt = "Кибернетический сигил и органические шипы"
                    }
                    isGenerating = true
                    scope.launch {
                        val result = GeminiTattooService.generateTattooConcept(
                            userPrompt = prompt,
                            style = selectedStyle,
                            zone = selectedZone
                        )
                        generatedResult = result
                        val newTattoo = TattooRepository.addGeneratedTattoo(
                            title = result.title,
                            style = selectedStyle,
                            zone = selectedZone,
                            description = result.styleDesc,
                            prompt = prompt
                        )
                        generatedTattooId = newTattoo.id
                        isGenerating = false
                        Toast.makeText(context, "Эскиз успешно создан и сохранен в ленту!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_tattoo_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("ИИ проектирует эскиз...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сгенерировать эскиз через ИИ", fontWeight = FontWeight.Bold)
                }
            }

            // Display Generated Result
            if (generatedResult != null && generatedTattooId != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = generatedResult!!.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = generatedResult!!.recommendedSize,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Generated visual preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF222633), Color(0xFF12141A))
                                    ),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(16.dp)
                        ) {
                            TattooArtRenderer(
                                patternType = "AI_CUSTOM",
                                modifier = Modifier.fillMaxSize(),
                                inkColor = Color(0xFFF1F3F8),
                                accentColor = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = generatedResult!!.styleDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Трансфер: ${generatedResult!!.stencilDetails}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Direct Try-On CTA for newly generated tattoo!
                        Button(
                            onClick = {
                                onTryOnGenerated(generatedTattooId!!)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("try_on_generated_cta"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(imageVector = Icons.Default.CenterFocusStrong, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Примерить эскиз на теле", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
