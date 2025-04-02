@file:Suppress("ktlint:standard:no-wildcard-imports")

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File

fun main() =
    application {
        Window(onCloseRequest = ::exitApplication, title = "Kotlin Script Runner") {
            App()
        }
    }

@Composable
fun App() {
    val outputLines = remember { mutableStateListOf<String>() }
    var isRunning by remember { mutableStateOf(false) }
    var lastExitCode by remember { mutableStateOf<Int?>(null) }
    var codeState by remember { mutableStateOf(TextFieldValue("")) }
    var highlightedLine by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kotlin Script Editor", fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))

//        val codeLines = codeState.split("\n")

        CodeEditor(
            codeState = codeState,
            onCodeChange = { codeState = it },
            highlightLine = highlightedLine,
        )

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                isRunning = true
                outputLines.clear()
                lastExitCode = null

                val scriptFile = File("temp.kts")
                scriptFile.writeText(codeState.text)

                val process =
                    ProcessBuilder(
                        "C:\\Users\\kajav\\Documents\\Programming Projects for Jobs\\JETBRAINS---CodeEditor\\kotlin-compiler-2.1.20\\kotlinc\\bin\\kotlinc.bat",
                        "-script",
                        scriptFile.absolutePath,
                    ).redirectErrorStream(true)
                        .start()

                val reader = process.inputStream.bufferedReader()
                Thread {
                    reader.forEachLine {
                        outputLines.add(it)
                    }
                    val exit = process.waitFor()
                    lastExitCode = exit
                    isRunning = false
                }.start()
            }) {
                Text("Run Script")
            }
            Text(if (isRunning) "Running..." else "Idle")
        }

        Spacer(Modifier.height(8.dp))

        Text("Output", fontSize = 16.sp)

        val scrollState = rememberScrollState()
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(8.dp)
                    .verticalScroll(scrollState),
        ) {
            Column {
                outputLines.forEach { line ->
                    val errorRegex = Regex("""(.+):(\d+):(\d+): error: (.+)""")
                    val match = errorRegex.find(line)

                    if (match != null) {
                        val (file, lineNum, colNum, message) = match.destructured
                        val annotatedText =
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.Red)) {
                                    append("$file:$lineNum:$colNum: error: ")
                                }
                                pushStringAnnotation(tag = "error_loc", annotation = "$lineNum:$colNum")
                                withStyle(
                                    SpanStyle(
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline,
                                    ),
                                ) {
                                    append(message)
                                }

                                pop()
                            }

                        ClickableText(
                            text = annotatedText,
                            onClick = { offset ->
                                annotatedText.getStringAnnotations("error_loc", offset, offset).firstOrNull()?.let { ann ->
                                    val (lineNumber, colNumber) = ann.item.split(":").map { it.toInt() }
                                    highlightedLine = lineNumber - 1
                                    val lines = codeState.text.split("\n")
                                    val position = lines.take(lineNumber - 1).sumOf { it.length + 1 } + (colNumber - 1)
                                    println("Clicked error: line=$lineNumber, col=$colNumber, offset=$position")
                                    // You can use this to later move the cursor
                                }
                            },
                        )
                    } else {
                        Text(line, color = Color.Green, fontSize = 13.sp)
                    }
                }
            }
        }

        lastExitCode?.let {
            Text(
                text = "Exit code: $it",
                color = if (it == 0) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
