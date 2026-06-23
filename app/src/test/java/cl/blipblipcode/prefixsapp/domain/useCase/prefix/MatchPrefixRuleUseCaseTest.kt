package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MatchPrefixRuleUseCaseTest {

    private lateinit var useCase: IMatchPrefixRuleUseCase

    @Before
    fun setUp() {
        useCase = MatchPrefixRuleUseCase()
    }

    @Test
    fun should_return_no_match_when_rules_are_empty_in_invoke() {
        //GIVEN
        val rules = emptyList<PrefixRule>()
        val phoneNumber = "1234567890"

        //WHEN
        val result = useCase(rules, phoneNumber)

        //THEN
        assertTrue(result is MatchResult.NoMatch)
    }

    @Test
    fun should_return_blocked_when_phone_matches_block_rule_in_invoke() {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "57", ruleType = PrefixRule.RuleType.BLOCK)
        )
        val phoneNumber = "573001234567"

        //WHEN
        val result = useCase(rules, phoneNumber)

        //THEN
        assertTrue(result is MatchResult.Blocked)
        val blocked = result as MatchResult.Blocked
        assertEquals("57", blocked.prefix)
    }

    @Test
    fun should_return_allowed_when_phone_matches_allow_rule_in_invoke() {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "1", ruleType = PrefixRule.RuleType.ALLOW)
        )
        val phoneNumber = "15551234567"

        //WHEN
        val result = useCase(rules, phoneNumber)

        //THEN
        assertTrue(result is MatchResult.Allowed)
        val allowed = result as MatchResult.Allowed
        assertEquals("1", allowed.prefix)
    }

    @Test
    fun should_choose_longest_matching_prefix_when_multiple_rules_match_in_invoke() {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "123", ruleType = PrefixRule.RuleType.BLOCK),
            PrefixRule(id = 2, prefix = "1234", ruleType = PrefixRule.RuleType.ALLOW)
        )
        val phoneNumber = "123456789"

        //WHEN
        val result = useCase(rules, phoneNumber)

        //THEN
        assertTrue(result is MatchResult.Allowed)
        val allowed = result as MatchResult.Allowed
        assertEquals("1234", allowed.prefix)
    }

    @Test
    fun should_return_no_match_when_no_rule_matches_in_invoke() {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "1", ruleType = PrefixRule.RuleType.BLOCK),
            PrefixRule(id = 2, prefix = "44", ruleType = PrefixRule.RuleType.BLOCK)
        )
        val phoneNumber = "573001234567"

        //WHEN
        val result = useCase(rules, phoneNumber)

        //THEN
        assertTrue(result is MatchResult.NoMatch)
    }

    @Test
    fun should_prefer_longest_match_even_if_shorter_block_matches_in_invoke() {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "57", ruleType = PrefixRule.RuleType.BLOCK),
            PrefixRule(id = 2, prefix = "5730", ruleType = PrefixRule.RuleType.ALLOW)
        )
        val phoneNumber = "573001234567"

        //WHEN
        val result = useCase(rules, phoneNumber)

        //THEN
        assertTrue(result is MatchResult.Allowed)
        val allowed = result as MatchResult.Allowed
        assertEquals("5730", allowed.prefix)
    }

    @Test
    fun should_strip_non_digits_from_phone_number_in_invoke() {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "57", ruleType = PrefixRule.RuleType.BLOCK)
        )
        val phoneNumber = "+57-300-123-4567"

        //WHEN
        val result = useCase(rules, phoneNumber)

        //THEN
        assertTrue(result is MatchResult.Blocked)
    }

    @Test
    fun should_strip_non_digits_from_rule_prefix_in_invoke() {
        //GIVEN
        val rules = listOf(
            PrefixRule(id = 1, prefix = "+57", ruleType = PrefixRule.RuleType.BLOCK)
        )
        val phoneNumber = "573001234567"

        //WHEN
        val result = useCase(rules, phoneNumber)

        //THEN
        assertTrue(result is MatchResult.Blocked)
    }
}
