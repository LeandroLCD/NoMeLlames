package cl.blipblipcode.prefixsapp.ui.home.model

data class Permission(
    val isEnabled: Boolean = false,
    val permissionsGranted: Boolean = false,
    val supportsRoleRequest: Boolean = true,
){
    val isActive: Boolean get() = permissionsGranted && isEnabled
}
