package cl.blipblipcode.prefixsapp.domain.useCase.version

import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import cl.blipblipcode.prefixsapp.domain.repositories.VersionRepository
import javax.inject.Inject

class CheckVersionStatusUseCase @Inject constructor(
    private val versionRepository: VersionRepository,
) : ICheckVersionStatusUseCase {
    override suspend operator fun invoke(): Result<VersionStatus> = versionRepository.checkVersionStatus()
}
