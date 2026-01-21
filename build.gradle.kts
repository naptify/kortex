import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform") version "2.3.0" apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.multiplatform")

    extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        mingwX64 {
            val libsDir = rootProject.file("libs")
            val inc = libsDir.resolve("include")
            val binDir = libsDir.resolve("windows")

            compilations.getByName("main").cinterops {
                val isCore = project.name == "core"
                val defName = if (isCore) "glfw" else "bgfx"

                create(defName) {
                    definitionFile.set(project.file("src/nativeInterop/cinterop/$defName.def"))
                    packageName("dev.naptify.kortex.$defName")
                    includeDirs(inc)

                    if (isCore) {
                        compilerOpts("-I$inc", "-D_GLFW_WIN32")
                    } else {
                        compilerOpts(
                            "-std=c++20",
                            "-I${inc.resolve("bgfx/c99")}",
                            "-I${inc.resolve("bx")}",
                            "-I${inc.resolve("bimg")}",
                            "-DBX_CONFIG_DEBUG=0", "-DBX_PLATFORM_WINDOWS=1",
                            "-D__STDC_CONSTANT_MACROS", "-D__STDC_FORMAT_MACROS", "-D__STDC_LIMIT_MACROS"
                        )
                    }
                }
            }

            binaries {
                all { linkerOpts("-L${binDir.absolutePath}") }

                if (project.name == "klyph") {
                    executable {
                        linkerOpts("-lbgfx", "-lbimg", "-lbimg_decode", "-lbx", "-lglfw3")
                        linkerOpts("-lgdi32", "-luser32", "-lpsapi", "-lcomdlg32", "-lole32", "-lshell32")
                    }
                } else if (project.name == "core") {
                    staticLib {
                        export(compilations.getByName("main").cinterops.getByName("glfw").dependencyFiles)
                    }
                }
            }
        }

        sourceSets {
            if (project.name == "klyph") {
                val commonMain by getting {
                    dependencies { implementation(project(":core")) }
                }
            }
        }
    }
}
