package cl.blipblipcode.prefixsapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen: NavKey {
    @Serializable
    data object Splash: Screen
    @Serializable
    data object Main: Screen
    @Serializable
    data object Permission: Screen
    @Serializable
    data object CriticalSetting: Screen
    @Serializable
    data object Security: Screen

}