import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

group = "dev.naptify.kortex"
version = "0.1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "2.3.0" apply false
    id("maven-publish")
}

publishing {
    publications {

    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.multiplatform")

    extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        mingwX64 {
            val inc = project.file("include")
            val bin = project.file("bin/windows")
            compilations.getByName("main").cinterops {
                when (project.name) {
                    "core" -> {
                        val glfw by creating {
                            definitionFile.set(project.file("src/nativeInterop/cinterop/glfw.def"))
                            packageName("dev.naptify.kortex.glfw")
                            includeDirs(inc)
                            compilerOpts(
                                "-I$inc",
                                "-D_GLFW_WIN32"
                            )
                        }
                    }
                    "klyph" -> {
                        val bgfx by creating {
                            definitionFile.set(project.file("src/nativeInterop/cinterop/bgfx.def"))
                            packageName("dev.naptify.kortex.bgfx")
                            includeDirs(inc, inc.resolve("bgfx/c99"))
                            compilerOpts(
                                "-std=c++20",
                                "-I${inc.resolve("bgfx/c99")}",
                                "-I${inc.resolve("bx")}",
                                "-I${inc.resolve("bimg")}",
                                "-DBX_CONFIG_DEBUG=0", "-DBX_PLATFORM_WINDOWS=1",
                                "-D__STDC_CONSTANT_MACROS", "-D__STDC_FORMAT_MACROS", "-D__STDC_LIMIT_MACROS",
                            )
                        }
                    }
                    else -> return@cinterops
                }
            }

            binaries {
                all { linkerOpts("-L${bin.absolutePath}") }

                when (project.name) {
                    "klyph" ->  {
                        executable {
                            linkerOpts("-lbgfx", "-lbimg", "-lbimg_decode", "-lbx", "-lglfw3")
                            linkerOpts("-lgdi32", "-luser32", "-lpsapi", "-lcomdlg32", "-lole32", "-lshell32")
                        }
                    }
                    "core" -> {
                        staticLib {
                            export(compilations.getByName("main").cinterops.getByName("glfw").dependencyFiles)
                        }
                    }
                }
            }
        }

        sourceSets {
            if (project.name != "core") {
                val commonMain by getting {
                    dependencies { implementation(project(":core")) }
                }
            }
        }
    }
}
