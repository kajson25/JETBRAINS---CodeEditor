@file:Suppress("ktlint:standard:no-wildcard-imports")

package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ui.components.CodeEditor
import ui.components.OutputConsole
import ui.components.ScriptRunner
import ui.components.TopBar

@Suppress("ktlint:standard:function-naming")
@Composable
fun MainScreen() {
    var code by remember { mutableStateOf("") }
    var codeState by remember { mutableStateOf(TextFieldValue("")) }
    val outputLines = remember { mutableStateListOf<String>() }
    var isRunning by remember { mutableStateOf(false) }
    var lastExitCode by remember { mutableStateOf<Int?>(null) }
    var highlightedLine by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopBar()
        Spacer(Modifier.height(8.dp))

        CodeEditor(
            codeState = codeState,
            onCodeChange = {
                codeState = it
                code = it.text
            },
            highlightLine = highlightedLine,
        )

        Spacer(Modifier.height(8.dp))

        ScriptRunner(
            code = code,
            onBeforeRun = {
                outputLines.clear()
                highlightedLine = null
                lastExitCode = null
            },
            onAppendOutputLine = { outputLines.add(it) },
            onRunningStateChange = { isRunning = it },
            onExitCodeUpdate = { lastExitCode = it },
        )

        Spacer(Modifier.height(8.dp))

        OutputConsole(
            outputLines = outputLines,
            onLineClick = { line ->
                println("Clicked error line: $line")
                highlightedLine = line
            },
        )

        lastExitCode?.let {
            Text(
                text = "Exit code: $it",
                color = if (it == 0) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
