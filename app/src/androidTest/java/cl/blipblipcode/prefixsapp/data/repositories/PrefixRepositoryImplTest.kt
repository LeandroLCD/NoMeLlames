package cl.blipblipcode.prefixsapp.data.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.entities.PrefixRuleEntity
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PrefixRepositoryImplTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: PrefixRepository

    @Inject
    lateinit var prefixRuleDao: PrefixRuleDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun should_emit_empty_set_when_no_prefixes_in_db_in_prefixes() = runTest {
        //WHEN
        val prefixes = repository.prefixes.first { true }

        //THEN
        assertEquals(emptySet<String>(), prefixes)
    }

    @Test
    fun should_emit_set_with_blocked_prefixes_when_db_has_rules_in_prefixes() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "ALLOW"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "44", ruleType = "BLOCK"))

        //WHEN
        val prefixes = repository.prefixes.first { it.isNotEmpty() }

        //THEN
        assertEquals(setOf("57", "44"), prefixes)
    }

    @Test
    fun should_emit_updated_set_when_block_prefix_added_after_subscription_in_prefixes() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "BLOCK"))

        //WHEN
        repository.prefixes.test {
            skipItems(1)
            assertEquals(setOf("1"), awaitItem())
            prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
            //THEN
            assertEquals(setOf("1", "57"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_empty_list_when_no_prefix_rules_in_db_in_get_all_invoke() = runTest {
        //WHEN
        val rules = repository.getAllPrefixRules().first()

        //THEN
        assertTrue(rules.isEmpty())
    }

    @Test
    fun should_emit_all_rules_ordered_by_created_at_desc_in_get_all_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "a", ruleType = "BLOCK", createdAt = 100L))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "b", ruleType = "BLOCK", createdAt = 300L))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "c", ruleType = "BLOCK", createdAt = 200L))

        //WHEN
        val rules = repository.getAllPrefixRules().first()

        //THEN
        assertEquals(listOf("b", "c", "a"), rules.map { it.prefix })
    }

    @Test
    fun should_emit_only_block_rules_in_get_blocked_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "ALLOW"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "44", ruleType = "BLOCK"))

        //WHEN
        val rules = repository.getBlockedPrefixes().first()

        //THEN
        assertEquals(setOf("57", "44"), rules.map { it.prefix }.toSet())
        assertTrue(rules.all { it.ruleType == PrefixRule.RuleType.BLOCK })
    }

    @Test
    fun should_emit_only_allow_rules_in_get_allowed_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "ALLOW"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "44", ruleType = "BLOCK"))

        //WHEN
        val rules = repository.getAllowedPrefixes().first()

        //THEN
        assertEquals(listOf("1"), rules.map { it.prefix })
        assertTrue(rules.all { it.ruleType == PrefixRule.RuleType.ALLOW })
    }

    @Test
    fun should_return_matching_rule_when_prefix_exists_in_get_prefix_by_value_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "ALLOW"))

        //WHEN
        val rule = repository.getPrefixByValue("57")

        //THEN
        assertNotNull(rule)
        assertEquals("57", rule?.prefix)
        assertEquals(PrefixRule.RuleType.BLOCK, rule?.ruleType)
    }

    @Test
    fun should_return_null_when_prefix_does_not_exist_in_get_prefix_by_value_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))

        //WHEN
        val rule = repository.getPrefixByValue("999")

        //THEN
        assertNull(rule)
    }

    @Test
    fun should_return_success_and_persist_rule_when_added_in_invoke() = runTest {
        //GIVEN
        val prefix = "57"
        val ruleType = PrefixRule.RuleType.BLOCK

        //WHEN
        val result = repository.addPrefixRule(prefix, ruleType)

        //THEN
        assertTrue(result.isSuccess)
        val rules = repository.getAllPrefixRules().first()
        assertEquals(1, rules.size)
        assertEquals(prefix, rules.first().prefix)
        assertEquals(ruleType, rules.first().ruleType)
    }

    @Test
    fun should_persist_both_rule_types_when_added_in_invoke() = runTest {
        //GIVEN
        val blockPrefix = "57"
        val allowPrefix = "1"

        //WHEN
        repository.addPrefixRule(blockPrefix, PrefixRule.RuleType.BLOCK)
        repository.addPrefixRule(allowPrefix, PrefixRule.RuleType.ALLOW)

        //THEN
        val rules = repository.getAllPrefixRules().first()
        assertEquals(2, rules.size)
        assertTrue(rules.any { it.prefix == blockPrefix && it.ruleType == PrefixRule.RuleType.BLOCK })
        assertTrue(rules.any { it.prefix == allowPrefix && it.ruleType == PrefixRule.RuleType.ALLOW })
    }

    @Test
    fun should_return_success_and_remove_rule_by_id_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "ALLOW"))
        val target = repository.getAllPrefixRules().first().first { it.prefix == "57" }

        //WHEN
        val result = repository.removePrefixRule(target.id)

        //THEN
        assertTrue(result.isSuccess)
        val rules = repository.getAllPrefixRules().first()
        assertEquals(1, rules.size)
        assertEquals("1", rules.first().prefix)
    }

    @Test
    fun should_return_success_and_remove_all_rules_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "ALLOW"))

        //WHEN
        val result = repository.deleteAllPrefixRules()

        //THEN
        assertTrue(result.isSuccess)
        assertTrue(repository.getAllPrefixRules().first().isEmpty())
    }

    @Test
    fun should_return_success_when_delete_all_called_on_empty_db_in_invoke() = runTest {
        //WHEN
        val result = repository.deleteAllPrefixRules()

        //THEN
        assertTrue(result.isSuccess)
        assertTrue(repository.getAllPrefixRules().first().isEmpty())
    }

    @Test
    fun should_return_success_and_remove_rule_by_value_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "ALLOW"))

        //WHEN
        val result = repository.removePrefixByValue("57")

        //THEN
        assertTrue(result.isSuccess)
        val rules = repository.getAllPrefixRules().first()
        assertEquals(1, rules.size)
        assertEquals("1", rules.first().prefix)
    }

    @Test
    fun should_return_success_when_remove_by_value_called_on_missing_value_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))

        //WHEN
        val result = repository.removePrefixByValue("999")

        //THEN
        assertTrue(result.isSuccess)
        assertEquals(1, repository.getAllPrefixRules().first().size)
    }

    @Test
    fun should_emit_true_by_default_when_no_value_persisted_in_skip_call_log() = runTest {
        //WHEN
        val value = repository.skipCallLog.first()

        //THEN
        assertEquals(true, value)
    }

    @Test
    fun should_emit_persisted_value_when_set_in_data_store_in_skip_call_log() = runTest {
        //GIVEN
        repository.setSkipCallLog(false)

        //WHEN
        val value = repository.skipCallLog.first { !it }

        //THEN
        assertEquals(false, value)
    }

    @Test
    fun should_emit_updated_value_after_set_skip_call_log_in_invoke() = runTest {
        //WHEN
        repository.skipCallLog.test {
            assertEquals(true, awaitItem())
            repository.setSkipCallLog(false)
            advanceUntilIdle()
            //THEN
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_true_by_default_when_no_value_persisted_in_skip_notification() = runTest {
        //WHEN
        val value = repository.skipNotification.first()

        //THEN
        assertEquals(true, value)
    }

    @Test
    fun should_emit_persisted_value_when_set_in_data_store_in_skip_notification() = runTest {
        //GIVEN
        repository.setSkipNotification(false)

        //WHEN
        val value = repository.skipNotification.first { !it }

        //THEN
        assertEquals(false, value)
    }

    @Test
    fun should_emit_updated_value_after_set_skip_notification_in_invoke() = runTest {
        //WHEN
        repository.skipNotification.test {
            assertEquals(true, awaitItem())
            repository.setSkipNotification(false)
            advanceUntilIdle()
            //THEN
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_keep_skip_flags_independent_when_one_is_set_in_invoke() = runTest {
        //GIVEN
        repository.setSkipCallLog(false)

        //WHEN
        val callLog = repository.skipCallLog.first { !it }
        val notification = repository.skipNotification.value

        //THEN
        assertEquals(false, callLog)
        assertEquals(true, notification)
    }

    @Test
    fun should_persist_block_rule_when_legacy_add_prefix_in_invoke() = runTest {
        //GIVEN
        val prefix = "57"

        //WHEN
        repository.addPrefix(prefix)
        advanceUntilIdle()

        //THEN
        val rules = repository.getAllPrefixRules().first()
        assertEquals(1, rules.size)
        assertEquals(prefix, rules.first().prefix)
        assertEquals(PrefixRule.RuleType.BLOCK, rules.first().ruleType)
    }

    @Test
    fun should_remove_rule_when_legacy_remove_prefix_in_invoke() = runTest {
        //GIVEN
        repository.addPrefix("57")

        //WHEN
        repository.removePrefix("57")

        //THEN
        val rules = repository.getAllPrefixRules().first { it.isEmpty() }
        assertTrue(rules.isEmpty())
    }
}
