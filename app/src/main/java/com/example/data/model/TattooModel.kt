package com.example.data.model

enum class TattooStyle(val displayName: String, val chipName: String) {
    ALL("Все стили", "Все"),
    MINIMALISM("Минимализм", "Минимализм"),
    FINE_LINE("Файн-лайн (Fine-line)", "Fine-line"),
    CYBER_SIGILISM("Кибер-сигилизм", "Cyber-sigil"),
    JAPANESE("Япония (Irezumi)", "Япония"),
    NEO_TRADITIONAL("Нео-традишнл", "Neo-Trad"),
    BLACKWORK("Блэкворк", "Blackwork"),
    GEOMETRY("Сакральная геометрия", "Геометрия"),
    BOTANICAL("Ботаника и Флора", "Ботаника"),
    TRASH_POLKA("Трэш-полька", "Trash Polka")
}

enum class BodyZone(val displayName: String, val painIndex: Int, val painDesc: String) {
    ALL("Все зоны", 0, ""),
    FOREARM("Предплечье", 3, "Низкая боль, идеальное место для первой татуировки"),
    WRIST("Запястье", 6, "Умеренная боль, чувствительно ближе к кости и сгибу"),
    COLLARBONE("Ключица", 8, "Высокая боль из-за близкого расположения кости и тонкой кожи"),
    SHOULDER("Плечо / Бицепс", 3, "Комфортная зона, минимальный дискомфорт"),
    RIBS("Ребра", 9, "Очень высокая чувствительность, нервные окончания и вибрация костей"),
    THIGH("Бедро", 4, "Умеренная боль, отличная зона для масштабных работ"),
    BACK("Спина / Лопатка", 6, "Средне-высокая боль, лопатка более чувствительна"),
    ANKLE("Лодыжка", 8, "Высокая боль, близко к кости и сухожилиям")
}

data class TattooItem(
    val id: String,
    val title: String,
    val artist: String,
    val style: TattooStyle,
    val targetBodyPart: BodyZone,
    val painLevel: Int, // 1..10
    val estimatedHours: String,
    val defaultSizeCm: String,
    val difficulty: String,
    val description: String,
    val tags: List<String>,
    val aspectRatio: Float, // for staggered grid variation: 0.8f .. 1.35f
    val artPatternType: String, // identifies drawing pattern in TattooArtRenderer
    val likesCount: Int = 120,
    val isSaved: Boolean = false,
    val customPrompt: String? = null
)

data class TattooBoard(
    val id: String,
    val title: String,
    val description: String,
    val coverTattooId: String? = null,
    val tattooIds: List<String> = emptyList()
)

data class SavedTryOn(
    val id: String,
    val tattooId: String,
    val bodyZone: BodyZone,
    val timestamp: Long,
    val notes: String = "",
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f
)
