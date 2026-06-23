package cl.blipblipcode.prefixsapp.ui.history

data class Export (
    val isExporting: Boolean = false,
    val exportedFilePath: String? = null,
    val exportErrorMessage: String? = null
)