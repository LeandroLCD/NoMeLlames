package cl.blipblipcode.prefixsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.blipblipcode.prefixsapp.data.local.entities.PrefixRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrefixRuleDao {

    @Query("SELECT * FROM prefix_rules ORDER BY createdAt DESC")
    fun getAllPrefixRules(): Flow<List<PrefixRuleEntity>>

    @Query("SELECT * FROM prefix_rules ORDER BY createdAt DESC")
    fun getAllPrefixRulesList(): List<PrefixRuleEntity>

    @Query("SELECT * FROM prefix_rules WHERE ruleType = 'BLOCK' ORDER BY createdAt DESC")
    fun getBlockedPrefixes(): Flow<List<PrefixRuleEntity>>

    @Query("SELECT * FROM prefix_rules WHERE ruleType = 'ALLOW' ORDER BY createdAt DESC")
    fun getAllowedPrefixes(): Flow<List<PrefixRuleEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM prefix_rules WHERE prefix = :prefix)")
    suspend fun existsPrefix(prefix: String): Boolean

    @Query("SELECT * FROM prefix_rules WHERE prefix = :prefix LIMIT 1")
    suspend fun getPrefixByValue(prefix: String): PrefixRuleEntity?

    @Insert
    suspend fun insertPrefixRule(prefixRule: PrefixRuleEntity)

    @Query("DELETE FROM prefix_rules WHERE id = :id")
    suspend fun deletePrefixRule(id: Long)

    @Query("DELETE FROM prefix_rules WHERE prefix = :prefix")
    suspend fun deletePrefixByValue(prefix: String)

    @Query("DELETE FROM prefix_rules")
    suspend fun deleteAllPrefixRules()

    @Query("SELECT COUNT(*) AS count, MAX(createdAt) AS lastTimestamp FROM prefix_rules")
    fun getPrefixStats(): Flow<PrefixRuleStats>
}