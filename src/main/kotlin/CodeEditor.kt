@file:Suppress("ktlint:standard:no-wildcard-imports")

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
            Column(
                modifier = Modifier.padding(end = 8.dp),
            ) {
                lines.forEachIndexed { i, _ ->
                    val isHighlighted = (i + 1) == highlightLine
                    val bgColor = if (isHighlighted) Color(0xFFFFE082) else Color.Transparent
                    Text(
                        text = "${i + 1}".padStart(3),
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier =
                            Modifier
                                .background(bgColor)
                                .padding(vertical = 2.dp)
                                .width(40.dp),
                    )
                }
            }

            // Highlighted + Real Input Overlay
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = highlightedText,
                    fontSize = 14.sp,
                    color = Color.Black,
                )
                BasicTextField(
                    value = codeState,
                    onValueChange = onCodeChange,
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Transparent),
                )
            }
        }
    }
}
