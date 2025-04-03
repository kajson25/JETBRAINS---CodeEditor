@file:Suppress("ktlint:standard:no-wildcard-imports")

package ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ui.helpers.ScriptLang
import java.io.File

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScriptRunner(
    code: String,
    lang: ScriptLang,
    onBeforeRun: () -> Unit,
    onAppendOutputLine: (String) -> Unit,
    onRunningStateChange: (Boolean) -> Unit,
    onExitCodeUpdate: (Int?) -> Unit,
) {
    var isRunning by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = {
            onBeforeRun()

            isRunning = true
            onRunningStateChange(true)

            val extension = if (lang == ScriptLang.KOTLIN) "kts" else "swift"
            val scriptFile = File("temp.$extension")
            scriptFile.writeText(code)

            val processBuilder =
                if (lang == ScriptLang.KOTLIN) {
                    ProcessBuilder("kotlin-compiler-2.1.20/kotlinc/bin/kotlinc.bat", "-script", scriptFile.absolutePath)
                } else {
                    ProcessBuilder("/usr/bin/env", "swift", scriptFile.absolutePath)
                }

            val process = processBuilder.redirectErrorStream(true).start()

            val reader = process.inputStream.bufferedReader()
            Thread {
                reader.forEachLine { line ->
                    onAppendOutputLine(line)
                }
                val exitCode = process.waitFor()
                onExitCodeUpdate(exitCode)
                onRunningStateChange(false)
            }.start()
        }) {
            Text("Run Script")
        }

        Text(if (isRunning) "Running..." else "Idle")
    }
}
