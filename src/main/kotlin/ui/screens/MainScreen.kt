@file:Suppress("ktlint:standard:no-wildcard-imports")

package ui.screens

import androidx.compose.foundation.background
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
import ui.helpers.ScriptLang
import java.io.File

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
    val editorBgColor = if (isDarkTheme) Color.Gray else Color.LightGray
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val highlightColor = if (isDarkTheme) Color(0xFFFFD54F) else Color.Yellow
    val historyStack = remember { mutableStateListOf<TextFieldValue>() }
    val redoStack = remember { mutableStateListOf<TextFieldValue>() }
    var currentLang by remember { mutableStateOf(ScriptLang.KOTLIN) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        if (it.isCtrlPressed && it.key == Key.F) {
                            isFindActive = true
                            true
                        } else if (it.isCtrlPressed && it.key == Key.Z && !it.isShiftPressed) {
                            if (historyStack.isNotEmpty()) {
                                redoStack.add(codeState)
                                val prev = historyStack.removeLast()
                                codeState = prev
                                code = prev.text
                            }
                            true
                        } else if (it.isCtrlPressed && it.isShiftPressed && it.key == Key.Z) {
                            if (redoStack.isNotEmpty()) {
                                historyStack.add(codeState)
                                val next = redoStack.removeLast()
                                codeState = next
                                code = next.text
                            }
                            true
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                },
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Sidebar
            ScriptSidebar(
                onScriptSelected = { content ->
                    code = content
                    codeState = TextFieldValue(content, TextRange(content.length))
                },
                onSaveScript = { path ->
                    File(path).writeText(code)
                },
                lang = if (currentLang == ScriptLang.KOTLIN) ScriptLang.SWIFT else ScriptLang.KOTLIN,
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(backgroundColor) // 👈 Add this line
                        .padding(16.dp),
            ) {
                TopBar(
                    isDark = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                    lang = currentLang,
                    onToggleLang = {
                        currentLang = if (currentLang == ScriptLang.KOTLIN) ScriptLang.SWIFT else ScriptLang.KOTLIN
                    },
                )

                if (isFindActive) {
                    FindBar(
                        query = findQuery,
                        totalMatches = matchPositions.size,
                        currentMatchIndex = currentMatchIndex,
                        onQueryChange = {
                            findQuery = it
                            matchPositions =
                                Regex(Regex.escape(it)).findAll(codeState.text).map { m -> m.range.first }.toList()
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

                        if (newText != oldText) {
                            historyStack.add(codeState) // store before change
                            redoStack.clear() // clear redo on new edit
                        }

                        if (newText.length > oldText.length) {
                            val insertedChar = newText.getOrNull(newTextFieldValue.selection.start - 1)

                            if (insertedChar == '\n') {
                                val cursorIndex = newTextFieldValue.selection.start
                                val prevLine = oldText.substring(0, cursorIndex - 1).split("\n").lastOrNull() ?: ""

                                val indent = if (prevLine.trimEnd().endsWith("{")) "    " else ""
                                val inserted = "\n$indent"

                                val updatedText =
                                    oldText.substring(
                                        0,
                                        cursorIndex - 1,
                                    ) + inserted + oldText.substring(cursorIndex - 1)
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
                    backgroundColor = editorBgColor,
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
                    lang = if (currentLang == ScriptLang.KOTLIN) ScriptLang.SWIFT else ScriptLang.KOTLIN,
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
}
