package cl.blipblipcode.prefixsapp.data.repositories.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionConfigDto(
    @SerialName("release") val release: String,
    @SerialName("min_supported_version") val minSupportedVersion: String,
    @SerialName("url_download") val urlDownload: String,
)
