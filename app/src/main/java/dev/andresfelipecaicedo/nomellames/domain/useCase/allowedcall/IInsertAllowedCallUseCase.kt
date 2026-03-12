package dev.andresfelipecaicedo.nomellames.domain.useCase.allowedcall

interface IInsertAllowedCallUseCase {
    suspend operator fun invoke(phoneNumber: String): Result<Unit>
}

