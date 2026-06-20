package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import cl.blipblipcode.prefixsapp.domain.repositories.VersionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeVersionRepository : VersionRepository {

    private val _versionStatusFlow = MutableSharedFlow<VersionStatus>(replay = 0, extraBufferCapacity = 16)
    private var checkResult: Result<VersionStatus> = Result.success(VersionStatus.UpToDate)

    override val versionStatusFlow: SharedFlow<VersionStatus> = _versionStatusFlow.asSharedFlow()

    override suspend fun checkVersionStatus(): Result<VersionStatus> = checkResult

    fun emit(status: VersionStatus) {
        _versionStatusFlow.tryEmit(status)
    }

    fun setCheckResult(result: Result<VersionStatus>) {
        checkResult = result
    }
}
