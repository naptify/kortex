rootProject.name = "kortex"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":core")
project(":core").projectDir = file("platform/core")
include(":koda")
project(":koda").projectDir = file("common/koda")
include(":kache")
project(":kache").projectDir = file("common/kache")
include(":kalculus")
project(":kalculus").projectDir = file("common/kalculus")
include(":klyph")
project(":klyph").projectDir = file("graphics/klyph")
include(":kinetic")
project(":kinetic").projectDir = file("engine/kinetic")
include(":kero")
project(":kero").projectDir = file("platform/kero")
include(":kult")
project(":kult").projectDir = file("engine/kult")
include(":krawl")
project(":krawl").projectDir = file("engine/krawl")