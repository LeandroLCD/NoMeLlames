package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

interface IInsertBlockedCallUseCase {
    suspend operator fun invoke(phoneNumber: String, matchedPrefix: String)
}

