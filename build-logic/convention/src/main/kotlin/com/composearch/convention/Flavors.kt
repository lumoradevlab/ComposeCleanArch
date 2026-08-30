/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package com.composearch.convention

import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension

/**
 * The build environments. Each becomes a product flavor on the app and exposes its
 * base URL through `BuildConfig.BASE_URL`, which the app's `NetworkConfig` reads — so
 * there is no hardcoded environment anywhere in code.
 *
 * **Rename these hosts for your app.** They are placeholders; the point is the shape
 * (one flavor per environment, URLs as BuildConfig fields, an applicationIdSuffix so
 * dev/staging/prod can be installed side by side).
 *
 * Need a second host (a CMS, an auth service)? Add a property here, emit it as another
 * `buildConfigField`, and expose it on `NetworkConfig` — don't hardcode it in a repository.
 */
@Suppress("EnumEntryName")
enum class AppFlavor(
    val applicationIdSuffix: String? = null,
    val baseUrl: String,
) {
    dev(applicationIdSuffix = ".dev", baseUrl = "https://api.dev.example.com/api/"),
    staging(applicationIdSuffix = ".staging", baseUrl = "https://api.staging.example.com/api/"),
    prod(baseUrl = "https://api.example.com/api/"),
}

internal fun configureFlavors(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        flavorDimensions += "environment"
        productFlavors {
            AppFlavor.values().forEach { flavor ->
                create(flavor.name) {
                    dimension = "environment"
                    buildConfigField("String", "BASE_URL", "\"${flavor.baseUrl}\"")
                    if (this is ApplicationProductFlavor) {
                        flavor.applicationIdSuffix?.let { applicationIdSuffix = it }
                    }
                }
            }
        }
    }
}
