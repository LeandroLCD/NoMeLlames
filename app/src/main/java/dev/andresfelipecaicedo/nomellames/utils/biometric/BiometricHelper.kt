package dev.andresfelipecaicedo.nomellames.utils.biometric

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Helper class that wraps BiometricPrompt to check device capability
 * and launch biometric or device-credential authentication.
 */
object BiometricHelper {

    /**
     * Checks whether the device has biometric hardware and the user has enrolled biometrics.
     */
    fun canAuthenticateWithBiometrics(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(Authenticators.BIOMETRIC_WEAK) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Checks whether the device has any device credential (PIN, pattern, password).
     */
    fun canAuthenticateWithDeviceCredential(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(Authenticators.DEVICE_CREDENTIAL) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Checks whether the device has any authentication method available.
     */
    fun hasAnyAuthMethod(context: Context): Boolean {
        return canAuthenticateWithBiometrics(context) || canAuthenticateWithDeviceCredential(context)
    }

    /**
     * Launches a biometric prompt. Falls back to device credential if biometric is unavailable.
     *
     * @param activity   The FragmentActivity host.
     * @param title      Dialog title.
     * @param subtitle   Dialog subtitle.
     * @param onSuccess  Called when authentication succeeds.
     * @param onError    Called when authentication fails or is cancelled.
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_TIMEOUT
                ) {
                    onError(errString.toString())
                }
            }
        }

        val canBiometric = canAuthenticateWithBiometrics(activity)
        val canDeviceCredential = canAuthenticateWithDeviceCredential(activity)

        if (!canBiometric && !canDeviceCredential) {
            onError("No hay métodos de autenticación disponibles")
            return
        }

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when {
                canBiometric && canDeviceCredential -> {
                    promptInfoBuilder.setAllowedAuthenticators(
                        Authenticators.BIOMETRIC_WEAK or Authenticators.DEVICE_CREDENTIAL
                    )
                }
                canBiometric -> {
                    promptInfoBuilder
                        .setAllowedAuthenticators(Authenticators.BIOMETRIC_WEAK)
                        .setNegativeButtonText("Cancelar")
                }
                else -> {
                    promptInfoBuilder.setAllowedAuthenticators(Authenticators.DEVICE_CREDENTIAL)
                }
            }
        } else {
            when {
                canBiometric && canDeviceCredential -> {
                    @Suppress("DEPRECATION")
                    promptInfoBuilder.setDeviceCredentialAllowed(true)
                }
                canBiometric -> {
                    @Suppress("DEPRECATION")
                    promptInfoBuilder.setDeviceCredentialAllowed(false)
                    promptInfoBuilder.setNegativeButtonText("Cancelar")
                }
                else -> {
                    @Suppress("DEPRECATION")
                    promptInfoBuilder.setDeviceCredentialAllowed(true)
                }
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfoBuilder.build())
    }
}
