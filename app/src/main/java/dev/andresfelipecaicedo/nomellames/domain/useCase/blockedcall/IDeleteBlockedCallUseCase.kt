package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

interface IDeleteBlockedCallUseCase {
    suspend operator fun invoke(id: Long)
}

