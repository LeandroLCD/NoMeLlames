package cl.blipblipcode.prefixsapp.domain.repositories

import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import kotlinx.coroutines.flow.SharedFlow

interface VersionRepository {
    val versionStatusFlow: SharedFlow<VersionStatus>
    suspend fun checkVersionStatus(): Result<VersionStatus>
}
