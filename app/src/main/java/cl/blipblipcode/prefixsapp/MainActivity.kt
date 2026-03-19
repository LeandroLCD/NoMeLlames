package cl.blipblipcode.prefixsapp

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.window.SplashScreenView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import cl.blipblipcode.prefixsapp.ui.navigation.NavGraph
import cl.blipblipcode.prefixsapp.ui.settings.SettingsViewModel
import cl.blipblipcode.prefixsapp.ui.theme.PrefixsAppTheme
import cl.blipblipcode.prefixsapp.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    var isCallScreeningEnabled by mutableStateOf(false)
    var permissionsGranted by mutableStateOf(false)
    private var legacyScreeningConfigured by mutableStateOf(false)
    
    // Security state
    private var isAuthenticated by mutableStateOf(false)
    var biometricLockEnabled by mutableStateOf(false)
    var patternLockEnabled by mutableStateOf(false)
    var storedPattern by mutableStateOf<List<Int>>(emptyList())

    val supportsCallScreeningRole: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private val settingsPrefs by lazy {
        getSharedPreferences(AppConstants.Prefs.NAME, MODE_PRIVATE)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions.values.all { it }
        when {
            permissionsGranted -> requestCallScreeningRole()
            permissions.entries.any { (permission, granted) ->
                !granted && !shouldShowRequestPermissionRationale(permission)
            } -> openAppSettings()
        }
    }

    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkCallScreeningRole()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener(SplashScreenView::remove)
        }else{
            setContentView(R.layout.main_activity)
        }

        legacyScreeningConfigured = settingsPrefs.getBoolean(AppConstants.Prefs.KEY_LEGACY_SCREENING_CONFIGURED, false)
        loadSecuritySettings()

        checkPermissionsAndRole()

        setContent {
            PrefixsAppTheme {
                NavGraph()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsAndRole()
        loadSecuritySettings()
    }
    
    private fun loadSecuritySettings() {
        biometricLockEnabled = settingsPrefs.getBoolean(SettingsViewModel.KEY_BIOMETRIC_LOCK, false)
        patternLockEnabled = settingsPrefs.getBoolean(SettingsViewModel.KEY_PATTERN_LOCK, false)
        storedPattern = loadStoredPattern()
    }
    
    private fun loadStoredPattern(): List<Int> {
        val patternString = settingsPrefs.getString(SettingsViewModel.KEY_PATTERN_VALUE, null) ?: return emptyList()
        return patternString.split(",").mapNotNull { it.toIntOrNull() }
    }

    private fun checkPermissionsAndRole() {
        permissionsGranted = checkSelfPermission(Manifest.permission.READ_PHONE_STATE) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_CALL_LOG) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

        checkCallScreeningRole()
    }

    fun checkCallScreeningRole() {
        if (supportsCallScreeningRole) {
            val roleManager = getSystemService(RoleManager::class.java)
            isCallScreeningEnabled = roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) == true
        } else {
            // Android 7-9: no hay API publica para confirmar este rol de forma fiable.
            isCallScreeningEnabled = permissionsGranted && legacyScreeningConfigured
        }
    }

    fun requestPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG
            )
        )
    }

    fun requestCallScreeningRole() {
        if (supportsCallScreeningRole) {
            val roleManager = getSystemService(RoleManager::class.java) ?: return
            if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
            ) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                roleRequestLauncher.launch(intent)
            }
            return
        }

        persistLegacyScreeningConfigured(true)
        openDefaultAppsSettings()
        checkCallScreeningRole()
    }

    fun persistLegacyScreeningConfigured(value: Boolean) {
        legacyScreeningConfigured = value
        settingsPrefs.edit { putBoolean(AppConstants.Prefs.KEY_LEGACY_SCREENING_CONFIGURED, value) }
    }

    internal fun openDefaultAppsSettings() {
        startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
    }

    internal fun openAppSettings() {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", packageName, null)
            }
        )
    }
}
