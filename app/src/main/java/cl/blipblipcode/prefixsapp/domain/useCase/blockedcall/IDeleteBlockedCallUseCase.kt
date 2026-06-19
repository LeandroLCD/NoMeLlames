package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

interface IDeleteBlockedCallUseCase {
    suspend operator fun invoke(id: Long)
}

