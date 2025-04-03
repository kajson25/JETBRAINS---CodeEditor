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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.helpers.highlightCode
import ui.helpers.kotlinKeywords

@Suppress("ktlint:standard:function-naming")
@Composable
fun CodeEditor(
    codeState: TextFieldValue,
    onCodeChange: (TextFieldValue) -> Unit,
    highlightLine: Int?,
    searchHighlights: List<Int> = emptyList(),
    backgroundColor: Color,
    textColor: Color,
    highlightColor: Color,
) {
    val scrollState = rememberScrollState()
    val lines = codeState.text.split("\n")

    val highlightedText =
        remember(codeState.text, codeState.selection, searchHighlights) {
            highlightCode(
                code = codeState.text,
                keywords = kotlinKeywords,
                cursor = codeState.selection.start,
                highlightRanges = searchHighlights,
                highlightColor = highlightColor,
            )
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(backgroundColor)
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
                    color = textColor,
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
