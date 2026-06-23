package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import javax.inject.Inject

class NormalizePhoneNumberUseCase @Inject constructor() : INormalizePhoneNumberUseCase {

    override fun invoke(phoneNumber: String, countryDialingCode: String?): String {
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        return if (countryDialingCode != null
            && digitsOnly.startsWith(countryDialingCode)
            && digitsOnly.length > countryDialingCode.length
        ) {
            digitsOnly.substring(countryDialingCode.length)
        } else {
            digitsOnly
        }
    }
}
