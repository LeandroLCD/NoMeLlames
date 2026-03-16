package cl.blipblipcode.prefixsapp.domain.model

import android.content.Context
import android.content.pm.PackageManager

enum class BuildType {
    Release,
    Debug,
    Apk;
    companion object{
        fun getBuildType(cxt: Context): BuildType{
            return try {
                val packageManager = cxt.packageManager
                val applicationInfo = packageManager.getApplicationInfo(cxt.packageName,
                    PackageManager.GET_META_DATA)
                when(applicationInfo.metaData.getString("cl.blipblipcode.prefixsapp.build")) {
                    "releas" -> Release
                    "apk" -> Apk
                    else -> Debug
                }

            }catch (e: Exception){
                e.printStackTrace()
                Debug
            }
        }
    }
}