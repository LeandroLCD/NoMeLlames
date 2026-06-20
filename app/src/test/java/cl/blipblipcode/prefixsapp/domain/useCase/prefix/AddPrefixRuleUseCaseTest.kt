package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import cl.blipblipcode.prefixsapp.domain.exception.PrefixAlreadyExistsException
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddPrefixRuleUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: IAddPrefixRuleUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = AddPrefixRuleUseCase(repository)
    }

    @Test
    fun should_return_success_when_prefix_is_valid_in_invoke() = runTest {
        //GIVEN
        repository.setAddPrefixRuleResult(Result.success(Unit))

        //WHEN
        val result = useCase("57", PrefixRule.RuleType.BLOCK)

        //THEN
        assertTrue(result.isSuccess)
        assertEquals("57", repository.lastAddedPrefixRule)
        assertEquals(PrefixRule.RuleType.BLOCK, repository.lastAddedRuleType)
    }

    @Test
    fun should_trim_whitespace_and_pass_clean_prefix_to_repository_in_invoke() = runTest {
        //GIVEN
        repository.setAddPrefixRuleResult(Result.success(Unit))

        //WHEN
        useCase("  57  ", PrefixRule.RuleType.BLOCK)

        //THEN
        assertEquals("57", repository.lastAddedPrefixRule)
    }

    @Test
    fun should_strip_non_digit_chars_and_pass_clean_prefix_to_repository_in_invoke() = runTest {
        //GIVEN
        repository.setAddPrefixRuleResult(Result.success(Unit))

        //WHEN
        useCase("abc57def", PrefixRule.RuleType.BLOCK)

        //THEN
        assertEquals("57", repository.lastAddedPrefixRule)
    }

    @Test
    fun should_return_failure_with_illegal_argument_when_clean_prefix_is_whitespace_only_in_invoke() = runTest {
        //GIVEN

        //WHEN
        val result = useCase("   ", PrefixRule.RuleType.BLOCK)

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun should_return_failure_with_illegal_argument_when_input_has_no_digits_in_invoke() = runTest {
        //GIVEN

        //WHEN
        val result = useCase("abc", PrefixRule.RuleType.BLOCK)

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun should_return_failure_with_already_exists_when_prefix_already_exists_in_repository_in_invoke() = runTest {
        //GIVEN
        repository.setAddPrefixRuleResult(Result.success(Unit))
        useCase("57", PrefixRule.RuleType.BLOCK)

        //WHEN
        val result = useCase("57", PrefixRule.RuleType.ALLOW)

        //THEN
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as? PrefixAlreadyExistsException
        assertTrue(ex != null)
        assertEquals("57", ex?.existingPrefix)
        assertEquals("BLOCK", ex?.existingRuleType)
    }

    @Test
    fun should_not_call_repository_add_when_prefix_is_empty_after_sanitization_in_invoke() = runTest {
        //GIVEN

        //WHEN
        useCase("", PrefixRule.RuleType.BLOCK)

        //THEN
        assertNull(repository.lastAddedPrefixRule)
        assertNull(repository.lastAddedRuleType)
    }

    @Test
    fun should_propagate_repository_failure_when_insert_fails_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("DB error")
        repository.setAddPrefixRuleResult(Result.failure(exception))

        //WHEN
        val result = useCase("1", PrefixRule.RuleType.BLOCK)

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
