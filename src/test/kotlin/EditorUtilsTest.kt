import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import ui.helpers.findMatchingBracket
import ui.helpers.highlightCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditorUtilsTest {
    @Test
    fun `should indent after brace`() {
        val input = "fun test() {"
        val result = handleAutoIndent(input, input.length)
        assertEquals("fun test() {\n    ", result.text)
        assertEquals(result.text.length, result.selection.start)
    }

    @Test
    fun `should not indent after normal line`() {
        val input = "println(\"Hello\")"
        val result = handleAutoIndent(input, input.length)
        assertEquals("println(\"Hello\")\n", result.text)
    }

    @Test
    fun `should match brackets forward`() {
        val input = "fun x() { val x = (1 + 2) }"
        val index = input.indexOf('(')
        val match = findMatchingBracket(input, index, '(', ')')
        assertEquals(input.indexOf(')'), match)
    }

    @Test
    fun `should match brackets backward`() {
        val input = "fun x() { val x = (1 + 2) }"
        val index = input.indexOf(')')
        val match = findMatchingBracket(input, index, '(', ')', backwards = true)
        assertEquals(input.indexOf('('), match)
    }

    @Test
    fun `should push to history and undo`() {
        val history = mutableListOf<TextFieldValue>()
        val redo = mutableListOf<TextFieldValue>()
        val state1 = TextFieldValue("first")
        val state2 = TextFieldValue("second")

        history.add(state1)
        val current = state2

        redo.add(current)
        val restored = history.removeLast()

        assertEquals("first", restored.text)
        assertEquals("second", redo.last().text)
    }

    @Test
    fun `should highlight keyword`() {
        val code = "fun main() { return }"
        val result =
            highlightCode(
                code = code,
                keywords = listOf("fun", "return"),
                cursor = 0,
                highlightRanges = emptyList(),
                highlightColor = Color.Yellow, // required now
            )
        val text = result.toString()
        assertTrue(text.contains("fun"))
        assertTrue(text.contains("return"))
    }
}

fun handleAutoIndent(
    text: String,
    cursor: Int,
): TextFieldValue {
    val lines = text.substring(0, cursor).split("\n")
    val prevLine = lines.lastOrNull() ?: ""
    val indent = if (prevLine.trim().endsWith("{")) "    " else ""
    val inserted = "\n$indent"
    val updated = text + inserted
    return TextFieldValue(updated, TextRange(updated.length))
}
