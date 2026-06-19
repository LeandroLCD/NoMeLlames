package cl.blipblipcode.prefixsapp.domain.useCase.version

import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import kotlinx.coroutines.flow.SharedFlow

interface IObserveVersionStatusUseCase {
    operator fun invoke(): SharedFlow<VersionStatus>
}