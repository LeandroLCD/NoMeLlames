package cl.blipblipcode.prefixsapp.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.model.maper.DomainMapper

@Entity(
    tableName = "prefix_rules",
    indices = [Index(value = ["prefix"], unique = true)]
)
data class PrefixRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prefix: String,
    val ruleType: String, // "BLOCK" or "ALLOW"
    val createdAt: Long = System.currentTimeMillis()
) : DomainMapper<PrefixRule> {
    
    override fun mapToDomain(): PrefixRule = PrefixRule(
        id = id,
        prefix = prefix,
        ruleType = PrefixRule.RuleType.valueOf(ruleType)
    )
    
    companion object {
        fun fromDomain(rule: PrefixRule): PrefixRuleEntity = PrefixRuleEntity(
            id = rule.id,
            prefix = rule.prefix,
            ruleType = rule.ruleType.name
        )
    }
}

