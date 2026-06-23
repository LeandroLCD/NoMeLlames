package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NormalizePhoneNumberUseCaseTest {

    private lateinit var useCase: INormalizePhoneNumberUseCase

    @Before
    fun setUp() {
        useCase = NormalizePhoneNumberUseCase()
    }

    @Test
    fun should_strip_non_digits_in_invoke() {
        //GIVEN
        val phoneNumber = "+57-300-123-4567"
        val countryCode: String? = null

        //WHEN
        val result = useCase(phoneNumber, countryCode)

        //THEN
        assertEquals("573001234567", result)
    }

    @Test
    fun should_remove_country_code_prefix_when_present_in_invoke() {
        //GIVEN
        val phoneNumber = "573001234567"
        val countryCode = "57"

        //WHEN
        val result = useCase(phoneNumber, countryCode)

        //THEN
        assertEquals("3001234567", result)
    }

    @Test
    fun should_keep_digits_when_country_code_does_not_match_in_invoke() {
        //GIVEN
        val phoneNumber = "15551234567"
        val countryCode = "57"

        //WHEN
        val result = useCase(phoneNumber, countryCode)

        //THEN
        assertEquals("15551234567", result)
    }

    @Test
    fun should_keep_digits_when_country_code_is_null_in_invoke() {
        //GIVEN
        val phoneNumber = "573001234567"
        val countryCode: String? = null

        //WHEN
        val result = useCase(phoneNumber, countryCode)

        //THEN
        assertEquals("573001234567", result)
    }

    @Test
    fun should_keep_digits_when_phone_length_equals_country_code_length_in_invoke() {
        //GIVEN
        val phoneNumber = "57"
        val countryCode = "57"

        //WHEN
        val result = useCase(phoneNumber, countryCode)

        //THEN
        assertEquals("57", result)
    }
}
