package cl.blipblipcode.prefixsapp.domain.useCase.version

import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import cl.blipblipcode.prefixsapp.domain.repositories.VersionRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ObserveVersionStatusUseCase @Inject constructor(
    private val versionRepository: VersionRepository
):IObserveVersionStatusUseCase {
    override fun invoke(): SharedFlow<VersionStatus> = versionRepository.versionStatusFlow

}