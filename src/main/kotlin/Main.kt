@file:Suppress("ktlint:standard:no-wildcard-imports")

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.screens.MainScreen

fun main() =
    application {
        Window(onCloseRequest = ::exitApplication, title = "Kotlin Script Runner") {
            MainScreen()
        }
    }
