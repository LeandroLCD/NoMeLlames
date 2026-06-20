package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetCachedPrefixRulesUseCaseTest {

    private lateinit var repository: FakePrefixRepository

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
    }

    @Test
    fun should_return_empty_list_when_no_load_has_been_triggered_in_invoke() = runTest {
        //GIVEN
        val useCase = GetCachedPrefixRulesUseCase(repository, this)

        //WHEN
        val first = useCase()

        //THEN
        assertTrue(first.isEmpty())
    }

    @Test
    fun should_return_cached_rules_after_load_completes_in_invoke() = runTest {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "57", ruleType = PrefixRule.RuleType.BLOCK),
            PrefixRule(id = 2, prefix = "1", ruleType = PrefixRule.RuleType.ALLOW)
        )
        repository.setAllPrefixRules(rules)
        val useCase = GetCachedPrefixRulesUseCase(repository, this)

        //WHEN
        useCase()
        advanceUntilIdle()
        val second = useCase()

        //THEN
        assertEquals(rules, second)
    }

    @Test
    fun should_return_empty_list_when_load_is_in_progress_in_invoke() = runTest {
        //GIVEN
        repository.setAllPrefixRules(
            listOf(PrefixRule(id = 1, prefix = "57", ruleType = PrefixRule.RuleType.BLOCK))
        )
        val useCase = GetCachedPrefixRulesUseCase(repository, this)

        //WHEN
        val first = useCase()

        //THEN
        assertTrue(first.isEmpty())
    }

    @Test
    fun should_return_same_reference_on_subsequent_calls_after_load_in_invoke() = runTest {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "57", ruleType = PrefixRule.RuleType.BLOCK)
        )
        repository.setAllPrefixRules(rules)
        val useCase = GetCachedPrefixRulesUseCase(repository, this)

        //WHEN
        useCase()
        advanceUntilIdle()
        val first = useCase()
        val second = useCase()

        //THEN
        assertSame(first, second)
    }

    @Test
    fun should_return_empty_list_when_repository_throws_in_invoke() = runTest {
        //GIVEN
        repository.shouldThrowOnGetAllPrefixRules = RuntimeException("DB error")
        val useCase = GetCachedPrefixRulesUseCase(repository, this)

        //WHEN
        val first = useCase()
        advanceUntilIdle()
        val second = useCase()

        //THEN
        assertTrue(first.isEmpty())
        assertTrue(second.isEmpty())
    }

    @Test
    fun should_trigger_reload_after_previous_load_fails_in_invoke() = runTest {
        //GIVEN
        repository.shouldThrowOnGetAllPrefixRules = RuntimeException("DB error")
        val useCase = GetCachedPrefixRulesUseCase(repository, this)
        useCase()
        advanceUntilIdle()
        val initialLoadCount = repository.getAllPrefixRulesCallCount

        //GIVEN — second attempt succeeds
        repository.shouldThrowOnGetAllPrefixRules = null
        repository.setAllPrefixRules(
            listOf(PrefixRule(id = 1, prefix = "57", ruleType = PrefixRule.RuleType.BLOCK))
        )

        //WHEN
        useCase()
        advanceUntilIdle()
        val result = useCase()

        //THEN
        assertTrue(result.isNotEmpty())
        assertTrue(repository.getAllPrefixRulesCallCount > initialLoadCount)
    }

    @Test
    fun should_not_trigger_multiple_loads_when_called_concurrently_in_invoke() = runTest {
        //GIVEN
        repository.setAllPrefixRules(
            listOf(PrefixRule(id = 1, prefix = "57", ruleType = PrefixRule.RuleType.BLOCK))
        )
        val useCase = GetCachedPrefixRulesUseCase(repository, this)

        //WHEN
        useCase()
        useCase()
        useCase()
        advanceUntilIdle()

        //THEN
        assertEquals(1, repository.getAllPrefixRulesCallCount)
    }
}
