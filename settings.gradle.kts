import java.net.URI

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl ("https://www.jitpack.io") }
    }
}

//sourceControl {
//    gitRepository(URI("https://github.com/ankidroid/Anki-Android.git")) {
//        producesModule("com.github.ankidroid:Anki-Android")
//    }
//}

rootProject.name = "ThaiToAnki"
include(":app")
 