package dev.andresfelipecaicedo.nomellames.data.local.static

import android.content.Context
import dev.andresfelipecaicedo.nomellames.utils.AppConstants
import org.json.JSONObject

object CountryDialingCodeProvider {

    @Volatile
    private var cache: Map<String, String>? = null

    fun getDialingCode(context: Context, countryIso: String?): String? {
        val normalizedIso = countryIso?.uppercase() ?: return null
        return loadCodes(context)[normalizedIso]
    }

    private fun loadCodes(context: Context): Map<String, String> {
        cache?.let { return it }

        return synchronized(this) {
            cache?.let { return it }

            val parsed = try {
                val json = context.assets.open(AppConstants.Assets.COUNTRY_DIALING_CODES_FILE).bufferedReader().use { it.readText() }
                val obj = JSONObject(json)
                buildMap {
                    obj.keys().forEach { key ->
                        put(key.uppercase(), obj.optString(key, ""))
                    }
                }.filterValues { it.isNotBlank() }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyMap()
            }

            cache = parsed
            parsed
        }
    }
}
