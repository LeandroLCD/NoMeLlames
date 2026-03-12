package dev.andresfelipecaicedo.nomellames.domain.model

data class PrefixRule(
    val id: Long = 0,
    val prefix: String,
    val ruleType: RuleType
) {
    enum class RuleType {
        BLOCK,
        ALLOW
    }
    
    val isBlocked: Boolean get() = ruleType == RuleType.BLOCK
    val isAllowed: Boolean get() = ruleType == RuleType.ALLOW
}

