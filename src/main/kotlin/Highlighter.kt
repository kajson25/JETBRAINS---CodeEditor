import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

fun highlightCode(
    code: String,
    keywords: List<String>,
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val words = code.split(Regex("(?<=\\s)|(?=\\s)")) // Keep whitespace as tokens

    for (word in words) {
        if (keywords.contains(word.trim())) {
            builder.pushStyle(SpanStyle(color = Color(0xFF007ACC))) // Blue for keywords
            builder.append(word)
            builder.pop()
        } else {
            builder.append(word)
        }
    }
    return builder.toAnnotatedString()
}
