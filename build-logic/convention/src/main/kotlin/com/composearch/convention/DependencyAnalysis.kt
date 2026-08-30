/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package com.composearch.convention

import org.gradle.api.Project

/**
 * Applies the dependency-analysis plugin to a module.
 *
 * Unlike some root-only plugins, dependency-analysis (2.x) must be applied to
 * BOTH the root project (done in the top-level build.gradle.kts, which owns the
 * aggregate `buildHealth` task) AND every subproject that should be analysed —
 * that per-module application is what produces each `:module:projectHealth`
 * report. Applying it here, from the base convention plugins, keeps every module
 * covered without repeating it in each build file.
 *
 * Applied by string id so build-logic needs no compile dependency on the plugin;
 * defaults are fine, so there's nothing to configure in Kotlin yet.
 */
internal fun Project.configureDependencyAnalysis() {
    pluginManager.apply("com.autonomousapps.dependency-analysis")
}
