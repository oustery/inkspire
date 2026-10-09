package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.BodyZone
import com.example.data.model.TattooStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiPlacementFeedback(
    val harmonyScore: Int, // 1..100
    val anatomicalAdvice: String,
    val painDetails: String,
    val durabilityNotes: String,
    val artistRecommendation: String
)

data class AiConceptResult(
    val title: String,
    val styleDesc: String,
    val recommendedSize: String,
    val stencilDetails: String,
    val artistPromptGuide: String
)

object GeminiTattooService {

    private const val TAG = "GeminiTattooService"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * AI Analysis of Tattoo placement on a specific body zone:
     * Evaluates muscle curvature, skin tension, pain sensitivity and longevity.
     */
    suspend fun analyzeBodyPlacement(
        tattooTitle: String,
        style: TattooStyle,
        zone: BodyZone,
        sizeCm: String
    ): AiPlacementFeedback = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val prompt = """
            Ты — опытный топ-мастер татуировки и специалист по анатомическому дизайну.
            Проанализируй размещение эскиза "$tattooTitle" (Стиль: ${style.displayName}) на зоне "${zone.displayName}".
            Примерный размер: $sizeCm.

            Дай ответ СТРОГО в формате JSON со следующими полями:
            {
              "harmonyScore": <целое число от 75 до 98>,
              "anatomicalAdvice": "<1-2 предложения о том, как линии эскиза согласуются с мускулами и изгибами этой зоны>",
              "painDetails": "<кратко об ощущениях в этой точке: сосуды, кость, время непрерывной работы>",
              "durabilityNotes": "<как поведет себя пигмент со временем: воздействие солнца, трение одежды, растяжение кожи>",
              "artistRecommendation": "<главный практический совет перед сеансом для клиента и мастера>"
            }
            Пиши на русском языке, стильно, профессионально, вдохновляюще.
        """.trimIndent()

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Intelligent fallback for offline / mock testing
            return@withContext getOfflinePlacementFeedback(style, zone)
        }

        try {
            val responseText = callGeminiRaw(prompt, apiKey)
            parsePlacementJson(responseText) ?: getOfflinePlacementFeedback(style, zone)
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API error, falling back to local master advice", e)
            getOfflinePlacementFeedback(style, zone)
        }
    }

    /**
     * AI Generation of a new tattoo concept from prompt and parameters.
     */
    suspend fun generateTattooConcept(
        userPrompt: String,
        style: TattooStyle,
        zone: BodyZone
    ): AiConceptResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val prompt = """
            Ты — ведущий креативный тату-дизайнер. Пользователь хочет создать новый эскиз татуировки.
            Запрос пользователя: "$userPrompt"
            Выбранный стиль: "${style.displayName}"
            Предполагаемая зона тела: "${zone.displayName}"

            Сгенерируй детальный концепт татуировки в формате JSON:
            {
              "title": "<лаконичное звучное название эскиза на русском>",
              "styleDesc": "<краткое эстетическое описание композиции и деталей>",
              "recommendedSize": "<оптимальный размер в см, например: 14 × 9 см>",
              "stencilDetails": "<описание контуров и игл для трансфера (например: 3RL контур, мягкий грейвош)>",
              "artistPromptGuide": "<готовое ТЗ для мастера или Midjourney/Imagen>"
            }
        """.trimIndent()

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineConcept(userPrompt, style, zone)
        }

        try {
            val responseText = callGeminiRaw(prompt, apiKey)
            parseConceptJson(responseText) ?: getOfflineConcept(userPrompt, style, zone)
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API concept error", e)
            getOfflineConcept(userPrompt, style, zone)
        }
    }

    private fun callGeminiRaw(prompt: String, apiKey: String): String {
        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", parts)
                })
            }
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("HTTP ${response.code}: ${response.message}")
        }

        val responseBodyStr = response.body?.string().orEmpty()
        val jsonRoot = JSONObject(responseBodyStr)
        val candidates = jsonRoot.getJSONArray("candidates")
        val firstCand = candidates.getJSONObject(0)
        val parts = firstCand.getJSONObject("content").getJSONArray("parts")
        return parts.getJSONObject(0).getString("text")
    }

    private fun parsePlacementJson(rawText: String): AiPlacementFeedback? {
        return try {
            val json = JSONObject(rawText.trim())
            AiPlacementFeedback(
                harmonyScore = json.optInt("harmonyScore", 92),
                anatomicalAdvice = json.optString("anatomicalAdvice"),
                painDetails = json.optString("painDetails"),
                durabilityNotes = json.optString("durabilityNotes"),
                artistRecommendation = json.optString("artistRecommendation")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseConceptJson(rawText: String): AiConceptResult? {
        return try {
            val json = JSONObject(rawText.trim())
            AiConceptResult(
                title = json.optString("title"),
                styleDesc = json.optString("styleDesc"),
                recommendedSize = json.optString("recommendedSize"),
                stencilDetails = json.optString("stencilDetails"),
                artistPromptGuide = json.optString("artistPromptGuide")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getOfflinePlacementFeedback(style: TattooStyle, zone: BodyZone): AiPlacementFeedback {
        val (harmony, advice) = when (zone) {
            BodyZone.FOREARM -> Pair(
                95,
                "Предплечье превосходно подчеркивает вертикальную динамику рисунка. Мышцы сгибателей создают естественное обрамление без сильных деформаций."
            )
            BodyZone.COLLARBONE -> Pair(
                93,
                "Линии ключицы идеально работают как направляющие оси. Рекомендуется дугообразное расположение для гармонии с плечевым поясом."
            )
            BodyZone.WRIST -> Pair(
                90,
                "Компактный фокус. Важно отступить 1.5-2 см от сгиба лучезапястного сустава, чтобы избежать перетирания пигмента браслетами."
            )
            BodyZone.SHOULDER -> Pair(
                96,
                "Куполообразная форма дельтовидной мышцы придает трехмерный объем и раскрывает глубину светотени."
            )
            BodyZone.RIBS -> Pair(
                91,
                "Изгиб ребер придает рисунку динамику при дыхании и движении корпуса. Требуется высокая точность натяжения кожи."
            )
            BodyZone.THIGH -> Pair(
                94,
                "Широкое анатомическое полотно. Позволяет мастеру максимально раскрыть тонкие градиенты и микродетализацию."
            )
            else -> Pair(
                92,
                "Сбалансированная посадка. Линии эскиза органично дополняют естественный мышечный рельеф."
            )
        }

        return AiPlacementFeedback(
            harmonyScore = harmony,
            anatomicalAdvice = advice,
            painDetails = "${zone.painDesc}. Оптимальное время непрерывной работы — 2.5-3 часа.",
            durabilityNotes = "Минимальная подверженность выгоранию при использовании SPF 50+. Пигмент стабилен.",
            artistRecommendation = "Перед сеансом увлажняйте кожу 3-4 дня, выспитесь и плотно поешьте за 1.5 часа до визита."
        )
    }

    private fun getOfflineConcept(userPrompt: String, style: TattooStyle, zone: BodyZone): AiConceptResult {
        return AiConceptResult(
            title = if (userPrompt.isNotBlank()) "Эскиз: ${userPrompt.take(24)}" else "Нео-сигил вдохновение",
            styleDesc = "Гармоничное сочетание акцентных линий в стиле ${style.displayName} с анатомической посадкой под зону ${zone.displayName}.",
            recommendedSize = "14 × 9 см",
            stencilDetails = "Контурные иглы 3RL / 5RL, микро-шейдинг, мягкие теневые переходы.",
            artistPromptGuide = "Татуировка $userPrompt, чистый контраст, векторная точность, без лишнего фонового шума."
        )
    }
}
