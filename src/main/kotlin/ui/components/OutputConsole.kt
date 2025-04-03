@file:Suppress("ktlint:standard:no-wildcard-imports")

package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("ktlint:standard:function-naming")
@Composable
fun OutputConsole(
    outputLines: List<String>,
    onLineClick: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier =
            Modifier
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
                            annotatedText
                                .getStringAnnotations("error_loc", offset, offset)
                                .firstOrNull()
                                ?.let { ann ->
                                    val (lineNumber, _) = ann.item.split(":").map { it.toInt() }
                                    onLineClick(lineNumber - 1)
                                }
                        },
                    )
                } else {
                    Text(line, color = Color.Green, fontSize = 13.sp)
                }
            }
        }
    }
}
