/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package com.composearch.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * Applies detekt to a module and wires it the same way everywhere: shared config
 * file, ktlint rules (via detekt-formatting), and machine-readable reports.
 *
 * Called from every base convention plugin (android.library / android.application
 * / jvm.library) so all modules are covered without each build file repeating it.
 *
 * Report-first: tasks run with `ignoreFailures = true` so introducing detekt to an
 * existing codebase surfaces findings without breaking the build. Flip it to `false`
 * (or wire a CI-only override) once the baseline is clean to make it a hard gate.
 */
internal fun Project.configureDetekt() {
    pluginManager.apply("io.gitlab.arturbosch.detekt")

    extensions.configure<DetektExtension> {
        // Our overrides on TOP of detekt's sensible defaults, so we only maintain
        // the deltas rather than a full copied ruleset.
        buildUponDefaultConfig = true
        parallel = true
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        // A baseline (once generated with `./gradlew detektBaseline`) lets us fail
        // on NEW issues while grandfathering existing ones.
        baseline = rootProject.file("config/detekt/baseline.xml").takeIf { it.exists() }
    }

    dependencies {
        "detektPlugins"(libs.findLibrary("detekt-formatting").get())
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "1.8"
        ignoreFailures = true
        reports {
            html.required.set(true)   // human-readable
            sarif.required.set(true)  // for CI / code-scanning uploads
            xml.required.set(false)
            txt.required.set(false)
            md.required.set(false)
        }
    }
    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = "1.8"
    }
}
