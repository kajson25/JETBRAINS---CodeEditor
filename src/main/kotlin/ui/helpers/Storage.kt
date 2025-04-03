package ui.helpers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight

val kotlinKeywords =
    listOf(
        "fun",
        "val",
        "var",
        "if",
        "else",
        "when",
        "for",
        "while",
        "return",
        "class",
        "if(",
        "for(",
        "}else{",
        "else{",
        "while(",
    )

fun highlightCodeWithBrackets(
    code: String,
    keywords: List<String>,
    cursor: Int,
): AnnotatedString {
    val builder = AnnotatedString.Builder()

    val pairs = mapOf('(' to ')', '{' to '}', '[' to ']')
    val openBrackets = pairs.keys
    val closeBrackets = pairs.values.toSet()

    var matchIndex: Int? = null

    // Detect cursor is next to a bracket
    val currentChar = code.getOrNull(cursor)
    val prevChar = code.getOrNull(cursor - 1)

    if (currentChar in openBrackets) {
        matchIndex = findMatchingBracket(code, cursor, currentChar!!, pairs[currentChar]!!)
    } else if (prevChar in closeBrackets) {
        val open = pairs.entries.find { it.value == prevChar }!!.key
        matchIndex = findMatchingBracket(code, cursor - 1, prevChar!!, open, backwards = true)
    }

    code.forEachIndexed { i, c ->
        val token = c.toString()
        val isKeywordStart = i == 0 || !code[i - 1].isLetterOrDigit()

        when {
            i == matchIndex || i == cursor || i == (cursor - 1) -> {
                builder.pushStyle(SpanStyle(color = Color.Magenta, fontWeight = FontWeight.Bold))
                builder.append(token)
                builder.pop()
            }
            keywords.any { it == token } && isKeywordStart -> {
                builder.pushStyle(SpanStyle(color = Color(0xFF007ACC)))
                builder.append(token)
                builder.pop()
            }
            else -> builder.append(token)
        }
    }

    return builder.toAnnotatedString()
}

fun findMatchingBracket(
    code: String,
    index: Int,
    open: Char,
    close: Char,
    backwards: Boolean = false,
): Int? {
    var balance = 0
    if (backwards) {
        for (i in index downTo 0) {
            when (code[i]) {
                close -> balance++
                open -> {
                    balance--
                    if (balance == 0) return i
                }
            }
        }
    } else {
        for (i in index until code.length) {
            when (code[i]) {
                open -> balance++
                close -> {
                    balance--
                    if (balance == 0) return i
                }
            }
        }
    }
    return null
}

fun highlightCode(
    code: String,
    keywords: List<String>,
    cursor: Int,
    highlightRanges: List<Int> = emptyList(),
    highlightColor: Color,
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val pairs = mapOf('(' to ')', '{' to '}', '[' to ']')
    val openBrackets = pairs.keys
    val closeBrackets = pairs.values.toSet()

    var matchIndex: Int? = null

    // Detect if cursor is next to a bracket
    val currentChar = code.getOrNull(cursor)
    val prevChar = code.getOrNull(cursor - 1)

    if (currentChar in openBrackets) {
        matchIndex = findMatchingBracket(code, cursor, currentChar!!, pairs[currentChar]!!)
    } else if (prevChar in closeBrackets) {
        val open = pairs.entries.find { it.value == prevChar }!!.key
        matchIndex = findMatchingBracket(code, cursor - 1, prevChar!!, open, backwards = true)
    }

    val words = code.split(Regex("(?<=\\s)|(?=\\s)")) // includes whitespace
    var index = 0

    for (word in words) {
        val range = index until index + word.length

        val isKeyword = keywords.contains(word.trim())
        val isBracket = range.contains(cursor) || range.contains(matchIndex)
        val isSearchMatch = highlightRanges.any { it in range }

        when {
            isBracket -> {
                builder.pushStyle(SpanStyle(color = Color.Magenta, fontWeight = FontWeight.Bold))
                builder.append(word)
                builder.pop()
            }
            isSearchMatch -> {
                builder.pushStyle(SpanStyle(background = highlightColor))
                builder.append(word)
                builder.pop()
            }
            isKeyword -> {
                builder.pushStyle(SpanStyle(color = Color(0xFF007ACC)))
                builder.append(word)
                builder.pop()
            }
            else -> builder.append(word)
        }

        index += word.length
    }

    return builder.toAnnotatedString()
}
