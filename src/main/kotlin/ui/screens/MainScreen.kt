@file:Suppress("ktlint:standard:no-wildcard-imports")

package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ui.components.*

@Suppress("ktlint:standard:function-naming")
@Composable
fun MainScreen() {
    var code by remember { mutableStateOf("") }
    var codeState by remember { mutableStateOf(TextFieldValue("")) }
    val outputLines = remember { mutableStateListOf<String>() }
    var isRunning by remember { mutableStateOf(false) }
    var lastExitCode by remember { mutableStateOf<Int?>(null) }
    var highlightedLine by remember { mutableStateOf<Int?>(null) }
    var findQuery by remember { mutableStateOf("") }
    var isFindActive by remember { mutableStateOf(false) }
    var matchPositions by remember { mutableStateOf<List<Int>>(emptyList()) }
    var currentMatchIndex by remember { mutableStateOf(0) }
    var isDarkTheme by remember { mutableStateOf(false) }
    val backgroundColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFF2F2F2)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val highlightColor = if (isDarkTheme) Color(0xFFFFD54F) else Color.Yellow

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .onPreviewKeyEvent {
                    if (it.isCtrlPressed && it.key == Key.F && it.type == KeyEventType.KeyDown) { // 33 = F
                        isFindActive = true
                        true
                    } else {
                        false
                    }
                },
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            TopBar(
                isDark = isDarkTheme,
                onToggleTheme = { isDarkTheme = !isDarkTheme },
            )

            if (isFindActive) {
                FindBar(
                    query = findQuery,
                    totalMatches = matchPositions.size,
                    currentMatchIndex = currentMatchIndex,
                    onQueryChange = {
                        findQuery = it
                        matchPositions = Regex(Regex.escape(it)).findAll(codeState.text).map { m -> m.range.first }.toList()
                        currentMatchIndex = 0
                        if (matchPositions.isNotEmpty()) {
                            val start = matchPositions[0]
                            codeState = codeState.copy(selection = TextRange(start, start))
                        }
                    },
                    onClose = {
                        isFindActive = false
                        findQuery = ""
                        matchPositions = emptyList()
                    },
                    onNext = {
                        if (matchPositions.isNotEmpty()) {
                            currentMatchIndex = (currentMatchIndex + 1) % matchPositions.size
                            val start = matchPositions[currentMatchIndex]
                            codeState = codeState.copy(selection = TextRange(start, start))
                        }
                    },
                    onPrev = {
                        if (matchPositions.isNotEmpty()) {
                            currentMatchIndex = (currentMatchIndex - 1 + matchPositions.size) % matchPositions.size
                            val start = matchPositions[currentMatchIndex]
                            codeState = codeState.copy(selection = TextRange(start, start))
                        }
                    },
                )
            }

            Spacer(Modifier.height(8.dp))

            CodeEditor(
                codeState = codeState,
                onCodeChange = { newTextFieldValue ->
                    val oldText = codeState.text
                    val newText = newTextFieldValue.text

                    if (newText.length > oldText.length) {
                        val insertedChar = newText.getOrNull(newTextFieldValue.selection.start - 1)

                        if (insertedChar == '\n') {
                            val cursorIndex = newTextFieldValue.selection.start
                            val prevLine = oldText.substring(0, cursorIndex - 1).split("\n").lastOrNull() ?: ""

                            val indent = if (prevLine.trimEnd().endsWith("{")) "    " else ""
                            val inserted = "\n$indent"

                            val updatedText =
                                oldText.substring(0, cursorIndex - 1) + inserted + oldText.substring(cursorIndex - 1)
                            val newOffset = cursorIndex - 1 + inserted.length

                            codeState =
                                TextFieldValue(
                                    text = updatedText,
                                    selection = TextRange(newOffset),
                                )
                            code = updatedText
                            return@CodeEditor
                        }
                    }

                    codeState = newTextFieldValue
                    code = newTextFieldValue.text
                },
                highlightLine = highlightedLine,
                searchHighlights = matchPositions,
                backgroundColor = backgroundColor,
                textColor = textColor,
                highlightColor = highlightColor,
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
}
