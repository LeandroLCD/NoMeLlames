package cl.blipblipcode.prefixsapp.domain.useCase.prefix

interface IDeleteAllPrefixRulesUseCase {
    suspend operator fun invoke(): Result<Unit>
}