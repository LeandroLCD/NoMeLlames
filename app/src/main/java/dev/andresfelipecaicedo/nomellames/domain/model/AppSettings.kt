package dev.andresfelipecaicedo.nomellames.domain.model

data class AppSettings(
    val lastPrefixUpdateTimestamp: Long,
    val totalPrefixCount: Int,
    val syncStatus: SyncStatus
) {
    enum class SyncStatus {
        NEVER,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
    
    /**
     * Calcula el progreso de actualización basado en qué tan reciente fue la última actualización.
     * - 100% si fue actualizado en las últimas 24 horas
     * - Decrece linealmente hasta 0% después de 30 días sin actualizar
     */
    fun calculateUpdateProgress(): Int {
        if (lastPrefixUpdateTimestamp == 0L) return 0
        
        val now = System.currentTimeMillis()
        val daysSinceUpdate = (now - lastPrefixUpdateTimestamp) / (1000 * 60 * 60 * 24)
        
        return when {
            daysSinceUpdate <= 1 -> 100
            daysSinceUpdate >= 30 -> 0
            else -> ((30 - daysSinceUpdate) * 100 / 30).toInt()
        }
    }
    fun getLastUpdate(): String? {
        if (lastPrefixUpdateTimestamp == 0L) return null

        val now = System.currentTimeMillis()
        val diffMillis = now - lastPrefixUpdateTimestamp
        val diffMinutes = diffMillis / (1000 * 60)
        val diffHours = diffMinutes / 60
        val remainingMinutes = diffMinutes % 60

        return when {
            diffMinutes < 60 -> "$diffMinutes MIN"
            else -> "$diffHours:${remainingMinutes.toString().padStart(2, '0')} HRS"
        }
    }
}

