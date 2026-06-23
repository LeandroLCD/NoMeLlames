package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllPrefixRulesUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: IGetAllPrefixRulesUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = GetAllPrefixRulesUseCase(repository)
    }

    @Test
    fun should_emit_repository_all_prefix_rules_in_invoke() = runTest {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "+57", ruleType = PrefixRule.RuleType.BLOCK),
            PrefixRule(id = 2, prefix = "+1", ruleType = PrefixRule.RuleType.ALLOW)
        )
        repository.setAllPrefixRules(rules)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(rules, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
