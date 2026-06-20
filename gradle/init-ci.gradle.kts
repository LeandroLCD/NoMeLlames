import java.io.File

gradle.projectsLoaded {
    val googleServicesFile = File(rootProject.projectDir, "app/google-services.json")

    if (!googleServicesFile.exists()) {
        val placeholder = """
            {
              "project_info": {
                "project_number": "000000000000",
                "project_id": "ci-placeholder",
                "storage_bucket": "ci-placeholder.appspot.com",
                "firebase_url": "https://ci-placeholder.firebaseio.com"
              },
              "client": [
                {
                  "client_info": {
                    "mobilesdk_app_id": "1:000000000000:android:0000000000000000000000",
                    "android_client_info": {
                      "package_name": "cl.blipblipcode.prefixsapp"
                    }
                  },
                  "oauth_client": [],
                  "api_key": [
                    {
                      "current_key": "AIzaSyDUMMY_KEY_FOR_CI_BUILDS_ONLY_000000000"
                    }
                  ],
                  "services": {
                    "appinvite_service": {
                      "other_platform_oauth_client": []
                    }
                  }
                }
              ],
              "configuration_version": "1"
            }
        """.trimIndent()
        googleServicesFile.writeText(placeholder)
        logger.lifecycle("[init-ci] Created placeholder app/google-services.json (no real config present)")
    }
}
