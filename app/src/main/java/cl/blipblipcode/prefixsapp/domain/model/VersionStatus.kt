package cl.blipblipcode.prefixsapp.domain.model

sealed class VersionStatus {
    data object UpToDate : VersionStatus()
    data class UpdateAvailable(val latestVersion: String, val urlDownload:String) : VersionStatus()
    data class UpdateRequired(val latestVersion: String, val urlDownload:String) : VersionStatus()
}
