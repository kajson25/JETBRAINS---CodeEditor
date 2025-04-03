@file:Suppress("ktlint:standard:no-wildcard-imports")

package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinKeywords

@Suppress("ktlint:standard:function-naming")
@Composable
fun CodeEditor(
    codeState: TextFieldValue,
    onCodeChange: (TextFieldValue) -> Unit,
    highlightLine: Int?,
) {
    val scrollState = rememberScrollState()
    val lines = codeState.text.split("\n")

    val highlightedText =
        remember(codeState.text) {
            highlightCode(codeState.text, kotlinKeywords)
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2F2F2))
                .padding(8.dp)
                .verticalScroll(scrollState),
    ) {
        Row {
            // Line numbers
            Column(modifier = Modifier.padding(end = 8.dp)) {
                lines.forEachIndexed { i, _ ->
                    val isHighlighted = i == highlightLine
                    val bg = if (isHighlighted) Color(0xFFFFE082) else Color.Transparent
                    Text(
                        text = "${i + 1}".padStart(3),
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier =
                            Modifier
                                .background(bg)
                                .padding(vertical = 2.dp)
                                .width(40.dp),
                    )
                }
            }

            // Overlay: highlighted + editable field
            Box {
                Text(
                    text = highlightedText,
                    fontSize = 14.sp,
                    color = Color.Black,
                )
                BasicTextField(
                    value = codeState,
                    onValueChange = onCodeChange,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Transparent),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

fun highlightCode(
    code: String,
    keywords: List<String>,
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val words = code.split(Regex("(?<=\\s)|(?=\\s)")) // split but keep spaces

    for (word in words) {
        if (keywords.contains(word.trim())) {
            builder.pushStyle(SpanStyle(color = Color(0xFF007ACC))) // Blue
            builder.append(word)
            builder.pop()
        } else {
            builder.append(word)
        }
    }

    return builder.toAnnotatedString()
}
