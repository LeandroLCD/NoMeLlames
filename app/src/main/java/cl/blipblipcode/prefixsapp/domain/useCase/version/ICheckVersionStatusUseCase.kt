package cl.blipblipcode.prefixsapp.domain.useCase.version

import cl.blipblipcode.prefixsapp.domain.model.VersionStatus

interface ICheckVersionStatusUseCase {
    suspend operator fun invoke(): Result<VersionStatus>
}
