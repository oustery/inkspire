package com.example.data.repository

import com.example.data.model.BodyZone
import com.example.data.model.SavedTryOn
import com.example.data.model.TattooBoard
import com.example.data.model.TattooItem
import com.example.data.model.TattooStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object TattooRepository {

    private val initialTattoos = listOf(
        TattooItem(
            id = "tat-1",
            title = "Кибер-сигил Дракон",
            artist = "Aura Cyber Studio",
            style = TattooStyle.CYBER_SIGILISM,
            targetBodyPart = BodyZone.FOREARM,
            painLevel = 3,
            estimatedHours = "2.5 ч",
            defaultSizeCm = "16 × 7 см",
            difficulty = "Высокая (острые линии)",
            description = "Футуристический кибернетический сигил с плавными органическими шипами в стиле Y2K / Neo-Goth. Идеально подчеркивает анатомический рельеф предплечья.",
            tags = listOf("кибер", "сигил", "Y2K", "предплечье", "черный"),
            aspectRatio = 1.25f,
            artPatternType = "CYBER_DRAGON",
            likesCount = 842,
            isSaved = true
        ),
        TattooItem(
            id = "tat-2",
            title = "Лунная Змея и Пионы",
            artist = "Elena FineLine",
            style = TattooStyle.FINE_LINE,
            targetBodyPart = BodyZone.COLLARBONE,
            painLevel = 8,
            estimatedHours = "3 ч",
            defaultSizeCm = "14 × 9 см",
            difficulty = "Средняя (микро-детали)",
            description = "Утонченная змея, скользящая сквозь лепестки цветущих пионов на фоне полумесяца. Выполнена тончайшей иглой 3RL с мягким точечным грейвошем.",
            tags = listOf("змея", "цветы", "луна", "файнлайн", "ключица"),
            aspectRatio = 0.85f,
            artPatternType = "SNAKE_FLOWER",
            likesCount = 1250,
            isSaved = true
        ),
        TattooItem(
            id = "tat-3",
            title = "Японский Кои в водопаде",
            artist = "Kenji Irezumi",
            style = TattooStyle.JAPANESE,
            targetBodyPart = BodyZone.SHOULDER,
            painLevel = 3,
            estimatedHours = "5 - 6 ч",
            defaultSizeCm = "22 × 14 см",
            difficulty = "Сложная (плотный закрас)",
            description = "Символ упорства и силы духа. Карп Кои, преодолевающий бурный поток, с традиционными волнами и лепестками сакуры в стиле классического японского Ирэдзуми.",
            tags = listOf("япония", "карп", "irezumi", "плечо", "традиция"),
            aspectRatio = 1.35f,
            artPatternType = "KOI_FISH",
            likesCount = 980,
            isSaved = false
        ),
        TattooItem(
            id = "tat-4",
            title = "Сакральный Компас & Мандала",
            artist = "Dmitry Geometry",
            style = TattooStyle.GEOMETRY,
            targetBodyPart = BodyZone.FOREARM,
            painLevel = 4,
            estimatedHours = "3.5 ч",
            defaultSizeCm = "13 × 13 см",
            difficulty = "Ювелирная точность",
            description = "Симметричная мандала с встроенной розой ветров и фрактальными элементами. Идеальная центровка и глубокий черный пигмент.",
            tags = listOf("геометрия", "мандала", "компас", "симметрия"),
            aspectRatio = 1.0f,
            artPatternType = "MANDALA_COMPASS",
            likesCount = 612,
            isSaved = false
        ),
        TattooItem(
            id = "tat-5",
            title = "Олдскульная Пантера",
            artist = "Iron Anchor Tattoo",
            style = TattooStyle.NEO_TRADITIONAL,
            targetBodyPart = BodyZone.THIGH,
            painLevel = 4,
            estimatedHours = "3 ч",
            defaultSizeCm = "18 × 12 см",
            difficulty = "Средняя (жирный контур)",
            description = "Классический оскал свирепой пантеры в традиционном стиле. Жирный плотный контур, насыщенный черный и яркие контрастные акценты.",
            tags = listOf("олдскул", "пантера", "traditional", "бедро"),
            aspectRatio = 1.15f,
            artPatternType = "PANTHER",
            likesCount = 745,
            isSaved = false
        ),
        TattooItem(
            id = "tat-6",
            title = "Минималистичный Кит & Звезды",
            artist = "Nova Studio",
            style = TattooStyle.MINIMALISM,
            targetBodyPart = BodyZone.WRIST,
            painLevel = 6,
            estimatedHours = "1 ч",
            defaultSizeCm = "7 × 4 см",
            difficulty = "Легкая",
            description = "Космический горбатый кит, плывущий среди звездных созвездий. Чистые лаконичные линии, идеально подходит для первого тату на запястье.",
            tags = listOf("минимализм", "кит", "космос", "запястье", "мини"),
            aspectRatio = 0.8f,
            artPatternType = "WHALE_STARS",
            likesCount = 1430,
            isSaved = true
        ),
        TattooItem(
            id = "tat-7",
            title = "Лисица Кицунэ и Огонь",
            artist = "Sakura Ink",
            style = TattooStyle.NEO_TRADITIONAL,
            targetBodyPart = BodyZone.BACK,
            painLevel = 6,
            estimatedHours = "5 ч",
            defaultSizeCm = "24 × 16 см",
            difficulty = "Высокая",
            description = "Мистическая девятихвостая лисица с традиционной маской и языками душевного пламени. Богатая динамика и роскошная композиция для спины.",
            tags = listOf("кицунэ", "лиса", "япония", "спина", "мистика"),
            aspectRatio = 1.3f,
            artPatternType = "KITSUNE_FOX",
            likesCount = 1890,
            isSaved = false
        ),
        TattooItem(
            id = "tat-8",
            title = "Цветущая Ветка Сакуры",
            artist = "Hana Botanica",
            style = TattooStyle.BOTANICAL,
            targetBodyPart = BodyZone.RIBS,
            painLevel = 9,
            estimatedHours = "2.5 ч",
            defaultSizeCm = "19 × 8 см",
            difficulty = "Высокая (тонкие градиенты)",
            description = "Изящная изогнутая ветвь вишни в цвету. Органично повторяет изгиб реберного рельефа тела, создавая эффект живого растения.",
            tags = listOf("сакура", "ботаника", "цветы", "ребра", "нежность"),
            aspectRatio = 1.4f,
            artPatternType = "BOTANICAL_SAKURA",
            likesCount = 1120,
            isSaved = false
        ),
        TattooItem(
            id = "tat-9",
            title = "Черный Ворон & Рунический Круг",
            artist = "Odin Darkworks",
            style = TattooStyle.BLACKWORK,
            targetBodyPart = BodyZone.FOREARM,
            painLevel = 3,
            estimatedHours = "3.5 ч",
            defaultSizeCm = "17 × 10 см",
            difficulty = "Плотный дотворк",
            description = "Мистический скандинавский ворон Хугин с расправленными крыльями в обрамлении рун старшего футарка. Глубокий матовый черный пигмент.",
            tags = listOf("ворон", "руны", "блэкворк", "викинги", "дотворк"),
            aspectRatio = 1.1f,
            artPatternType = "RAVEN_RUNES",
            likesCount = 890,
            isSaved = true
        ),
        TattooItem(
            id = "tat-10",
            title = "Бабочка Morpho с геометрией",
            artist = "Morpho Art Lab",
            style = TattooStyle.FINE_LINE,
            targetBodyPart = BodyZone.COLLARBONE,
            painLevel = 7,
            estimatedHours = "1.5 ч",
            defaultSizeCm = "8 × 6 см",
            difficulty = "Микро-линии",
            description = "Симметричная бабочка, правое крыло которой переходит в сакральные геометрические лучи и орбиты. Невесомый воздушный дизайн.",
            tags = listOf("бабочка", "ключица", "геометрия", "файнлайн"),
            aspectRatio = 0.9f,
            artPatternType = "BUTTERFLY_GEO",
            likesCount = 1670,
            isSaved = false
        ),
        TattooItem(
            id = "tat-11",
            title = "Самурай в стиле Трэш-Полька",
            artist = "Berlin Rebel Ink",
            style = TattooStyle.TRASH_POLKA,
            targetBodyPart = BodyZone.SHOULDER,
            painLevel = 4,
            estimatedHours = "4.5 ч",
            defaultSizeCm = "20 × 13 см",
            difficulty = "Экспрессивная графика",
            description = "Дерзкое сочетание реалистичного шлема Кабуто, типографики, геометрических штрихов и брызг ярко-красного акрила.",
            tags = listOf("трэшполька", "самурай", "красный", "плечо", "графика"),
            aspectRatio = 1.25f,
            artPatternType = "TRASH_SAMURAI",
            likesCount = 760,
            isSaved = false
        ),
        TattooItem(
            id = "tat-12",
            title = "Таро: Карта Солнца",
            artist = "Arcana Mystica",
            style = TattooStyle.MINIMALISM,
            targetBodyPart = BodyZone.WRIST,
            painLevel = 5,
            estimatedHours = "1.5 ч",
            defaultSizeCm = "9 × 5 см",
            difficulty = "Средняя",
            description = "Гравюрная рамка карты Таро с сияющим солнцем и лучами. Символ жизненной энергии, успеха и внутреннего света.",
            tags = listOf("таро", "солнце", "гравюра", "запястье", "мистика"),
            aspectRatio = 1.35f,
            artPatternType = "TAROT_SUN",
            likesCount = 1340,
            isSaved = false
        )
    )

    private val initialBoards = listOf(
        TattooBoard(
            id = "board-1",
            title = "Хочу набить (Приоритет)",
            description = "Эскизы для ближайшей записи в студию",
            coverTattooId = "tat-1",
            tattooIds = listOf("tat-1", "tat-2", "tat-6", "tat-9")
        ),
        TattooBoard(
            id = "board-2",
            title = "Кибер и Графика",
            description = "Острые сигилы и темный абстрактный стиль",
            coverTattooId = "tat-1",
            tattooIds = listOf("tat-1", "tat-4", "tat-9", "tat-11")
        ),
        TattooBoard(
            id = "board-3",
            title = "Нежность и Файн-лайн",
            description = "Микро-линии для ключиц и запястья",
            coverTattooId = "tat-2",
            tattooIds = listOf("tat-2", "tat-6", "tat-10", "tat-12")
        )
    )

    private val _tattoos = MutableStateFlow(initialTattoos)
    val tattoos: StateFlow<List<TattooItem>> = _tattoos.asStateFlow()

    private val _boards = MutableStateFlow(initialBoards)
    val boards: StateFlow<List<TattooBoard>> = _boards.asStateFlow()

    private val _savedTryOns = MutableStateFlow<List<SavedTryOn>>(emptyList())
    val savedTryOns: StateFlow<List<SavedTryOn>> = _savedTryOns.asStateFlow()

    fun getTattooById(id: String): TattooItem? {
        return _tattoos.value.find { it.id == id }
    }

    fun toggleLike(id: String) {
        _tattoos.value = _tattoos.value.map { item ->
            if (item.id == id) {
                item.copy(likesCount = item.likesCount + 1)
            } else item
        }
    }

    fun toggleSave(id: String) {
        _tattoos.value = _tattoos.value.map { item ->
            if (item.id == id) {
                val newSaved = !item.isSaved
                item.copy(isSaved = newSaved)
            } else item
        }
    }

    fun addGeneratedTattoo(
        title: String,
        style: TattooStyle,
        zone: BodyZone,
        description: String,
        prompt: String
    ): TattooItem {
        val newId = "gen-" + UUID.randomUUID().toString().take(6)
        val newItem = TattooItem(
            id = newId,
            title = title,
            artist = "AI Studio Engine",
            style = style,
            targetBodyPart = zone,
            painLevel = zone.painIndex,
            estimatedHours = "2 - 3 ч",
            defaultSizeCm = "12 × 10 см",
            difficulty = "Средняя (ИИ-эскиз)",
            description = description,
            tags = listOf("ai-design", style.chipName.lowercase(), zone.displayName.lowercase()),
            aspectRatio = 1.1f,
            artPatternType = "AI_CUSTOM",
            likesCount = 1,
            isSaved = true,
            customPrompt = prompt
        )
        _tattoos.value = listOf(newItem) + _tattoos.value
        return newItem
    }

    fun saveTryOn(tattooId: String, zone: BodyZone, scale: Float, rotation: Float, notes: String = "") {
        val tryOn = SavedTryOn(
            id = "try-" + UUID.randomUUID().toString().take(6),
            tattooId = tattooId,
            bodyZone = zone,
            timestamp = System.currentTimeMillis(),
            notes = notes,
            scale = scale,
            rotation = rotation
        )
        _savedTryOns.value = listOf(tryOn) + _savedTryOns.value
    }
}
