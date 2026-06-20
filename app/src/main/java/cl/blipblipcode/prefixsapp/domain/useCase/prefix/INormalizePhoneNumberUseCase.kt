package cl.blipblipcode.prefixsapp.domain.useCase.prefix

interface INormalizePhoneNumberUseCase {
    operator fun invoke(phoneNumber: String, countryDialingCode: String?): String
}
